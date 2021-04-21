package io.cygnus.indicator.impl.base;

import java.time.ZonedDateTime;

import io.horizon.market.data.MarketData;
import io.horizon.market.serial.TimePointSerial;

public abstract class FloatPeriodPoint<M extends MarketData> extends BasePoint<TimePointSerial, M> {

	protected FloatPeriodPoint(int index, TimePointSerial serial) {
		super(index, serial);
	}

	public ZonedDateTime getTimePoint() {
		return serial.getTimePoint();
	}

}