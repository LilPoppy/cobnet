package com.storechain;

import java.util.Map.Entry;

public class KeyValuePair<T, E> implements Entry<T, E> {
	
	private T _key;
	
	private E _value;
	
	public KeyValuePair(T key, E value) {
		this._key = key;
		this._value = value;
	}

	public T getKey() {
		return this._key;
	}

	public E getValue() {
		return this._value;
	}

	public E setValue(E value) {
		this._value = value;
		return this._value;
	}
	
	
//	public static <T,E> KeyValuePair<T,E> valueOf(T key, E value) {
//		return new KeyValuePair<T,E>(key, value);
//	}
	


}
