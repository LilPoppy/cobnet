package com.storechain.polyglot;

import java.lang.reflect.Type;

import org.graalvm.polyglot.TypeLiteral;

public class PolyglotTypeLiteral<T> {

	public final TypeLiteral<T> literal;
	
	public PolyglotTypeLiteral(TypeLiteral<T> literal) {
		
		this.literal = literal;
	}
	
	public TypeLiteral<T> getLiteral() {
		
		return this.literal;
	}
	
	@Override
	public boolean equals(Object obj) {
		
		return this.literal.equals(obj);
	}
	
	public Class<T> getRawType() {
		
		return this.literal.getRawType();
	}
	
	public Type getType() {
		
		return this.literal.getType();
	}
	
	@Override
	public int hashCode() {
		
		return this.literal.hashCode();
	}
	
	@Override
	public String toString() {
		
		return this.literal.toString();
	}
	
}
