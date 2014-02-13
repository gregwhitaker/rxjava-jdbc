package com.github.davidmoten.rx.jdbc;

/**
 * Utility methods to checking conditions.
 */
final class Conditions {

	/**
	 * If and only if parameter is false throw a {@link RuntimeException}.
	 * 
	 * @param b
	 */
	static void checkTrue(boolean b) {
		if (!b)
			throw new RuntimeException("check failed");
	}

}
