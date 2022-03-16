package com.cobnet.polyglot.options;

import java.util.function.Consumer;
import java.util.function.Function;

import org.graalvm.options.OptionType;

public final class PolyglotOptionType<T> {

	private final OptionType<T> type;
	
	public PolyglotOptionType(OptionType<T> type) {
		
		this.type = type;
	}
	
	public PolyglotOptionType(String name, Function<String,T> stringConverter) {
		
		this(new OptionType<T>(name, stringConverter));
	}
	
	public PolyglotOptionType(String name, Function<String,T> stringConverter, Consumer<T> validator) {
		
		this(new OptionType<T>(name, stringConverter, validator));
	}

	public OptionType<T> getType() {
		
		return type;
	}
	
	public T convert(Object previousValue, String nameSuffix, String value) {
		
		return this.type.convert(previousValue, nameSuffix, value);
	}
	
	public T convert(String value) {
		
		return this.type.convert(value);
	}
	
	public static <T> PolyglotOptionType<T>	defaultType(Class<T> clazz) {
		
		return new PolyglotOptionType<T>(OptionType.defaultType(clazz));
	}
	
	public static <T> PolyglotOptionType<T>	defaultType(T value) {
		
		return new PolyglotOptionType<T>(OptionType.defaultType(value));
	}
	
	public String getName() {
		
		return this.type.getName();
	}
	
	@Override
	public String toString() {
		
		return this.type.toString();
	}
	
	public void	validate(T value) {
		
		this.type.validate(value);
	}
}
