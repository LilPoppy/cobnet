package com.storechain.common;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
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
	
	
	public static <T,E> BiFunction<T, E, Void> adapt(final BiConsumer<T,E> consumer) {
		
		return (t,e) -> {
			consumer.accept(t, e);
			return null;
		};
	}
	
}
