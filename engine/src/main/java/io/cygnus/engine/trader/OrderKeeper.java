package io.cygnus.engine.trader;

import static io.mercury.common.collections.MutableMaps.newIntObjectHashMap;

import java.io.Serializable;

import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import org.eclipse.collections.api.map.primitive.MutableIntObjectMap;
import org.slf4j.Logger;

import io.horizon.market.data.impl.BasicMarketData;
import io.horizon.market.instrument.Instrument;
import io.horizon.trader.account.Account;
import io.horizon.trader.account.SubAccount;
import io.horizon.trader.order.ChildOrder;
import io.horizon.trader.order.OrdSysIdAllocator;
import io.horizon.trader.order.Order;
import io.horizon.trader.order.enums.OrdType;
import io.horizon.trader.order.enums.TrdAction;
import io.horizon.trader.order.enums.TrdDirection;
import io.horizon.trader.transport.outbound.OrderReport;
import io.mercury.common.collections.Capacity;
import io.mercury.common.log.Log4j2LoggerFactory;

/**
 * 统一管理订单<br>
 * OrderBookManager只对订单进行存储<br>
 * 根据订单状态存储在不同的分区<br>
 * 不对订单进行处理<br>
 * 
 * @author yellow013
 */

@NotThreadSafe
public final class OrderKeeper implements Serializable {

	private static final long serialVersionUID = 8581377004396461013L;

	/*
	 * Logger
	 */
	private static final Logger log = Log4j2LoggerFactory.getLogger(OrderKeeper.class);

	/*
	 * 存储所有的order
	 */
	private static final OrderBook AllOrders = new OrderBook(Capacity.L09_SIZE);

	/*
	 * 按照subAccountId分组存储
	 */
	private static final MutableIntObjectMap<OrderBook> SubAccountOrders = newIntObjectHashMap();

	/*
	 * 按照accountId分组存储
	 */
	private static final MutableIntObjectMap<OrderBook> AccountOrders = newIntObjectHashMap();

	/*
	 * 按照strategyId分组存储
	 */
	private static final MutableIntObjectMap<OrderBook> StrategyOrders = newIntObjectHashMap();

	/*
	 * 按照instrumentId分组存储
	 */
	private static final MutableIntObjectMap<OrderBook> InstrumentOrders = newIntObjectHashMap();

	private OrderKeeper() {
	}

	/**
	 * 新增订单
	 * 
	 * @param order
	 */
	private static void putOrder(Order order) {
		int subAccountId = order.getSubAccountId();
		int accountId = order.getAccountId();
		AllOrders.putOrder(order);
		getSubAccountOrderBook(subAccountId).putOrder(order);
		getAccountOrderBook(accountId).putOrder(order);
		getStrategyOrderBook(order.getStrategyId()).putOrder(order);
		getInstrumentOrderBook(order.getInstrument()).putOrder(order);
	}

	/**
	 * 
	 * @param order
	 */
	private static void updateOrder(Order order) {
		switch (order.getStatus()) {
		case Filled:
		case Canceled:
		case NewRejected:
		case CancelRejected:
			AllOrders.finishOrder(order);
			getSubAccountOrderBook(order.getSubAccountId()).finishOrder(order);
			getAccountOrderBook(order.getAccountId()).finishOrder(order);
			getStrategyOrderBook(order.getStrategyId()).finishOrder(order);
			getInstrumentOrderBook(order.getInstrument()).finishOrder(order);
			break;
		default:
			log.info("Not need processed, strategyId==[{}], ordSysId==[{}], status==[{}]", order.getStrategyId(),
					order.getOrdSysId(), order.getStatus());
			break;
		}
	}

	/**
	 * 处理订单回报
	 * 
	 * @param report
	 * @return
	 */
	public static ChildOrder handleOrderReport(OrderReport report) {
		log.info("Handle OrdReport, report -> {}", report);
		// 根据订单回报查找所属订单
		Order order = getOrder(report.getOrdSysId());
		if (order == null) {
			// 处理订单由外部系统发出而收到报单回报的情况
			log.warn("Received other source order, ordSysId==[{}]", report.getOrdSysId());
			// 根据成交回报创建新订单, 放入OrderBook托管
			order = ChildOrder.newExternalOrder(report);
			// 新订单放入OrderBook
			putOrder(order);
		} else
			order.writeLog(log, "OrderBookKeeper :: Search order OK");
		ChildOrder childOrder = (ChildOrder) order;
		// 根据订单回报更新订单状态
		OrderUpdater.updateWithReport(childOrder, report);
		// 更新Keeper内订单
		updateOrder(childOrder);
		return childOrder;
	}

	/**
	 * 
	 * @param ordSysId
	 * @return
	 */
	public static boolean isContainsOrder(long ordSysId) {
		return AllOrders.isContainsOrder(ordSysId);
	}

	/**
	 * 
	 * @param ordSysId
	 * @return
	 */
	@Nullable
	public static Order getOrder(long ordSysId) {
		return AllOrders.getOrder(ordSysId);
	}

	/**
	 * 
	 * @param subAccountId
	 * @return
	 */
	public static OrderBook getSubAccountOrderBook(int subAccountId) {
		return SubAccountOrders.getIfAbsentPut(subAccountId, OrderBook::new);
	}

	/**
	 * 
	 * @param accountId
	 * @return
	 */
	public static OrderBook getAccountOrderBook(int accountId) {
		return AccountOrders.getIfAbsentPut(accountId, OrderBook::new);
	}

	/**
	 * 
	 * @param strategyId
	 * @return
	 */
	public static OrderBook getStrategyOrderBook(int strategyId) {
		return StrategyOrders.getIfAbsentPut(strategyId, OrderBook::new);
	}

	/**
	 * 
	 * @param instrument
	 * @return
	 */
	public static OrderBook getInstrumentOrderBook(Instrument instrument) {
		return InstrumentOrders.getIfAbsentPut(instrument.getInstrumentId(), OrderBook::new);
	}

	public static void onMarketData(BasicMarketData marketData) {
		// TODO 处理行情
	}

	/**
	 * 创建[ParentOrder], 并存入Keeper
	 * 
	 * @param ordSysIdAllocator
	 * @param strategyId
	 * @param subAccount
	 * @param account
	 * @param instrument
	 * @param offerQty
	 * @param offerPrice
	 * @param type
	 * @param direction
	 * @param action
	 * @return
	 */
	public static ChildOrder createAndSaveChildOrder(OrdSysIdAllocator ordSysIdAllocator, int strategyId,
			SubAccount subAccount, Account account, Instrument instrument, int offerQty, long offerPrice, OrdType type,
			TrdDirection direction, TrdAction action) {
		ChildOrder childOrder = ChildOrder.newOrder(ordSysIdAllocator, strategyId, subAccount, account, instrument,
				offerQty, offerPrice, type, direction, action);
		putOrder(childOrder);
		return childOrder;
	}

//	/**
//	 * 将[ParentOrder]转换为[ChildOrder], 并存入Keeper
//	 * 
//	 * @param parentOrder
//	 * @return
//	 */
//	public static ChildOrder toChildOrder(ParentOrder parentOrder) {
//		ChildOrder childOrder = parentOrder.toChildOrder();
//		putOrder(childOrder);
//		return childOrder;
//	}
//
//	/**
//	 * 将[ParentOrder]拆分为多个[ChildOrder], 并存入Keeper
//	 * 
//	 * @param parentOrder
//	 * @param count
//	 * @return
//	 */
//	public static MutableList<ChildOrder> splitChildOrder(ParentOrder parentOrder, int count) {
//		MutableList<ChildOrder> childOrders = parentOrder.splitChildOrder(order -> {
//			// TODO
//			return null;
//		});
//		childOrders.each(OrderBookKeeper::putOrder);
//		return childOrders;
//	}

	@Override
	public String toString() {
		return "";
	}

}
