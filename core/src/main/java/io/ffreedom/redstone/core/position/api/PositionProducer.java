package io.ffreedom.redstone.core.position.api;

@FunctionalInterface
public interface PositionProducer<T extends Position> {

	default T produce(int accountId, int instrumentId) {
		return produce(accountId, instrumentId, 0L);
	}

	T produce(int accountId, int instrumentId, long qty);

}
