package io.mercury.redstone.example;

import java.util.Properties;

import io.mercury.common.concurrent.disruptor.BufferSize;
import io.mercury.common.datetime.DateTimeUtil;
import io.mercury.common.log.LogLevel;
import io.mercury.common.log.LoggerSetter;
import io.mercury.common.param.map.ImmutableParamMap;
import io.mercury.financial.instrument.futures.impl.ChinaFutures;
import io.mercury.financial.instrument.futures.impl.ChinaFuturesSymbol;
import io.mercury.financial.time.TimePeriodPool;
import io.mercury.financial.time.TradingPeriodPool;
import io.mercury.financial.vector.TimePeriod;
import io.mercury.ftdc.adaptor.FtdcAdaptor;
import io.mercury.ftdc.adaptor.FtdcAdaptorParam;
import io.mercury.redstone.core.adaptor.Adaptor;
import io.mercury.redstone.core.keeper.AdaptorKeeper;
import io.mercury.redstone.core.keeper.InstrumentKeeper;
import io.mercury.redstone.core.keeper.StrategyKeeper;
import io.mercury.redstone.core.strategy.StrategyScheduler;
import io.mercury.redstone.engine.scheduler.SpscQueueStrategyScheduler;

public final class StartExample {

	private static int appId = 1;

	public static void main(String[] args) {
		long datetime = DateTimeUtil.datetimeOfSecond();
		LoggerSetter.logFileName("redstone-" + appId + "-" + datetime);
		LoggerSetter.logLevel(LogLevel.DEBUG);

		// TODO 读取配置文件
		Properties properties = null;

		// Set Global AppId

		StrategyScheduler scheduler = new SpscQueueStrategyScheduler(BufferSize.POW2_12);

		ImmutableParamMap<FtdcAdaptorParam> adaptorParam = new ImmutableParamMap<>(FtdcAdaptorParam.values(),
				properties);

		// 创建InboundAdaptor
		int inboundAdaptorId = 1;
		String inboundAdaptorName = "Ctp-InboundAdaptor";
		// TODO ADD ACCOUNT

		Adaptor adaptor = new FtdcAdaptor(inboundAdaptorId, inboundAdaptorName, null, scheduler, adaptorParam);

		TimePeriodPool.Singleton.register(ChinaFuturesSymbol.values(), TimePeriod.values());

		TradingPeriodPool.Singleton.register(ChinaFuturesSymbol.values());

		ChinaFutures rb1910 = new ChinaFutures(ChinaFuturesSymbol.RB, 1910);

		InstrumentKeeper.putInstrument(rb1910);

		int strategyId = 1;
		int subAccountId = 1;

		SmaStrategyExample example = new SmaStrategyExample(strategyId, subAccountId, rb1910);
		example.initialize(() -> true);

		StrategyKeeper.putStrategy(example);
		AdaptorKeeper.putAdaptor(adaptor);

		adaptor.startup();

	}

}