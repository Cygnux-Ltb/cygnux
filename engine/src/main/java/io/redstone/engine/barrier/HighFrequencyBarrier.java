package io.redstone.barrier;

import org.slf4j.Logger;

import io.ffreedom.common.log.CommonLoggerFactory;
import io.redstone.core.barrier.OrderBarrier;
import io.redstone.core.order.impl.ChildOrder;

public final class HighFrequencyBarrier implements OrderBarrier<ChildOrder> {

	private Logger logger = CommonLoggerFactory.getLogger(getClass());

	@Override
	public boolean filter(ChildOrder order) {
		switch (order.getSide().direction()) {
		case Long:
			
			return false;
		
		case Short:

			return false;

		default:
			logger.error("");
			return false;
		}
	}
	
	public static void main(String[] args) {
		
		System.out.println(Integer.MAX_VALUE);
		
		
	}

}