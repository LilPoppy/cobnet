package com.storechain.polyglot.options;

import java.util.Map;
import java.util.Set;

import org.graalvm.options.OptionMap;

public final class PolyglotOptionMap<T> {

	private final OptionMap<T> map;
	
	public PolyglotOptionMap(OptionMap<T> map) {
		
		this.map = map;
	}

	public OptionMap<T> getMap() {
		
		return map;
	}
	
	public static <T> PolyglotOptionMap<T> empty() {
		
		return new PolyglotOptionMap<T>(OptionMap.empty());
	}
	
	public Set<Map.Entry<String,T>> entrySet() {
		
		return this.map.entrySet();
	}
	
	@Override
	public boolean equals(Object obj) {
		
		return this.map.equals(obj);
	}
	
	public T get(String key) {
		
		return this.map.get(key);
	}
	
	@Override
	public int hashCode() {
		
		return this.map.hashCode();
	}
}
