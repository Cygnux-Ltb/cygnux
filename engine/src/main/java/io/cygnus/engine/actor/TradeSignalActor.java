package io.cygnus.engine.actor;

import org.eclipse.collections.api.list.MutableList;

import io.horizon.definition.order.TrdSignal;
import io.mercury.common.collections.MutableLists;

public final class TradeSignalActor {

	private MutableList<TrdSignal> tradeSignalList = MutableLists.newFastList(256);

	public static final TradeSignalActor Singleton = new TradeSignalActor();

	public boolean addTradeSignal(TrdSignal signal) {
		return tradeSignalList.add(signal);
	}

	public void handleTradeSignal(TrdSignal signal) {
		switch (signal.action()) {
		case Open:
			switch (signal.direction()) {
			case Long:
				
				break;
			case Short:
				
				break;

			default:
				break;
			}
			break;
		case Close:
			switch (signal.direction()) {
			case Long:

				break;

			case Short:

				break;

			default:
				break;
			}

			break;
		default:
			break;
		}

	}

}