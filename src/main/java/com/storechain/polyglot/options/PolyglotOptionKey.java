package com.storechain.polyglot.options;

import org.graalvm.options.OptionType;
import org.graalvm.options.OptionKey;
import org.graalvm.options.OptionValues;

public final class PolyglotOptionKey<T> {

	private final OptionKey<T> key;
	
	public PolyglotOptionKey(OptionKey<T> key) {
		
		this.key = key;
	}
	
	public PolyglotOptionKey(T defaultValue) {
		
		this(new OptionKey<T>(defaultValue));
	}
	
	public PolyglotOptionKey(T defaultValue, OptionType<T> type) {
		
		this(new OptionKey<T>(defaultValue, type));
	}
	
	public PolyglotOptionKey(T defaultValue, PolyglotOptionType<T> type) {
		
		this(new OptionKey<T>(defaultValue, type.getType()));
	}

	public OptionKey<T> getKey() {
		
		return key;
	}
	
	public T getDefaultValue() {
		
		return this.key.getDefaultValue();
	}
	
	public PolyglotOptionType<T> getType() {
		
		return new PolyglotOptionType<T>(this.key.getType());
	}
	
	public T getValue(OptionValues values) {
		
		return this.key.getValue(values);
	}
	
	public T getValue(PolyglotOptionValues values) {
		
		return this.getValue(values.getValues());
	}
	
	public boolean hasBeenSet(OptionValues values) {
		
		return this.key.hasBeenSet(values);
	}
	
	public boolean hasBeenSet(PolyglotOptionValues values) {
		
		return this.hasBeenSet(values.getValues());
	}
	
	public static <V> PolyglotOptionKey<PolyglotOptionMap<V>> mapOf(Class<V> valueClass) {
		
		return new PolyglotOptionKey<PolyglotOptionMap<V>>(new PolyglotOptionMap<V>(OptionKey.mapOf(valueClass).getDefaultValue()));
	}
}
