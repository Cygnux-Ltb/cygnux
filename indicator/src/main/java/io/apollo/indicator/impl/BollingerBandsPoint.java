package io.apollo.indicator.impl;

import io.apollo.indicator.impl.base.FixedPeriodPoint;
import io.horizon.definition.market.data.impl.BasicMarketData;
import io.horizon.definition.market.vector.TimePeriodSerial;

public final class BollingerBandsPoint extends FixedPeriodPoint<BasicMarketData> {

	private BollingerBandsPoint(int index, TimePeriodSerial timePeriod) {
		super(index, timePeriod);
	}

	@Override
	protected void handleMarketData0(BasicMarketData marketData) {

	}

}
