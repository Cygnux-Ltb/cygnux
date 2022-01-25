package io.cygnus.engine.scheduler;

import io.horizon.market.api.MarketData;
import io.horizon.trader.handler.InboundHandler;
import io.horizon.trader.strategy.Strategy;

/**
 * 
 * @author yellow013
 *
 * @param <M>
 */
public interface MultiStrategyScheduler<M extends MarketData> extends InboundHandler<M> {

	void addStrategy(Strategy<M> strategy);

}
