package com.storechain.common;

import java.util.function.Consumer;
import java.util.function.Function;

public final class CommonConverter {

	private CommonConverter() {}
	
	public static <T> Function<T, Void> adapt(final Consumer<T> consumer) {
		
	    return t -> {
	        consumer.accept(t);
	        return null;
	    };
	}
}
