package io.redstone.adaptor.simulator;

import java.util.List;

import io.ffreedom.common.functional.Converter;
import io.ffreedom.common.param.ParamKeyMap;
import io.ffreedom.transport.core.api.Receiver;
import io.ffreedom.transport.socket.SocketReceiver;
import io.ffreedom.transport.socket.config.SocketConfigurator;
import io.mercury.persistence.avro.serializable.AvroBytesDeserializer;
import io.polaris.financial.market.impl.BasicMarketData;
import io.redstone.adaptor.simulator.converter.MarketDataConverter;
import io.redstone.adaptor.simulator.converter.OrderConverter;
import io.redstone.core.adaptor.impl.InboundAdaptor;
import io.redstone.core.order.impl.OrderReport;
import io.redstone.core.strategy.StrategyScheduler;
import io.redstone.persistence.avro.entity.MarketDataLevel1;
import io.redstone.persistence.avro.entity.Order;

public class SimInboundAdaptor extends InboundAdaptor {

	private Receiver mdReceiver;

	private Receiver tdReceiver;

	private StrategyScheduler scheduler;

	protected SocketConfigurator mdConfigurator;

	protected SocketConfigurator tdConfigurator;

	private Converter<MarketDataLevel1, BasicMarketData> marketDataConverter = new MarketDataConverter();

	private Converter<Order, OrderReport> orderConverter = new OrderConverter();

	private AvroBytesDeserializer<MarketDataLevel1> marketDataDeserializer = new AvroBytesDeserializer<>(
			MarketDataLevel1.class);

	private AvroBytesDeserializer<Order> orderDeserializer1 = new AvroBytesDeserializer<>(Order.class);

	public SimInboundAdaptor(int adaptorId, String adaptorName, ParamKeyMap<SimAdaptorParams> paramMap,
			StrategyScheduler scheduler) {
		super(adaptorId, adaptorName);
		this.scheduler = scheduler;
		this.mdReceiver = new SocketReceiver(mdConfigurator, (bytes) -> {
			List<MarketDataLevel1> marketDatas = marketDataDeserializer.deSerializationMultiple(bytes);
			for (MarketDataLevel1 marketData : marketDatas) {
				scheduler.onMarketData(marketDataConverter.convert(marketData));
			}
		});
		this.tdReceiver = new SocketReceiver(tdConfigurator, (bytes) -> {
			List<Order> orders = orderDeserializer1.deSerializationMultiple(bytes);
			for (Order order : orders) {
				scheduler.onOrderReport(orderConverter.convert(order));
			}
		});
	}

	@Override
	public boolean activate() {
		mdReceiver.receive();
		tdReceiver.receive();
		return mdReceiver.isConnected() && tdReceiver.isConnected();
	}

	@Override
	public String getAdaptorName() {
		return "SimInboundAdaptor$" + this.hashCode();
	}

	@Override
	public boolean close() {
		mdReceiver.destroy();
		tdReceiver.destroy();
		return false;
	}

}