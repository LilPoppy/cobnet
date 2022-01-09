package com.storechain.polyglot;

import org.graalvm.polyglot.Value;

import java.nio.ByteOrder;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Set;

import org.graalvm.polyglot.proxy.Proxy;

import org.graalvm.polyglot.TypeLiteral;

public final class PolyglotValue {

	private final Value value;
	
	public PolyglotValue(Value value) {
		
		this.value = value;
	}
	
	public PolyglotValue(Object o) {
		
		this.value = Value.asValue(o);
	}
	
	public Value getValue() {
		
		return this.value;
	}
	
	public <T> T as(Class<T> targetType) {
		
		return this.value.as(targetType);
	}
	
	public <T> T as(PolyglotTypeLiteral<T> targetType) {
		
		return this.value.as(targetType.getLiteral());
	}
	
	public <T> T as(TypeLiteral<T> targetType) {
		
		return this.value.as(targetType);
	}
	
	public Boolean asBoolean() {
		
		return this.value.asBoolean();
	}
	
	public byte asByte() {
		
		return this.value.asByte();
	}
	
	public LocalDate asDate() {
		
		return this.value.asDate();
	}
	
	public Double asDouble() {
		
		return this.value.asDouble();
	}
	
	public Duration asDuration() {
		
		return this.value.asDuration();
	}
	
	public float asFloat() {
		
		return this.value.asFloat();
	}
	
	@SuppressWarnings("unchecked")
	public <T> T asHostObject() {
		
		return (T) this.value.asHostObject();
	}
	
	public Instant asInstant() {
		
		return this.value.asInstant();
	}
	
	public int asInt() {
		
		return this.value.asInt();
	}
	
	public long asLong() {
		
		return this.value.asLong();
	}
	
	public long asNativePointer() {
		
		return this.value.asNativePointer();
	}
	
	public <T extends Proxy> T asProxyObject() {
		
		return this.value.asProxyObject();
	}
	
	public short asShort() {
		
		return this.value.asShort();
	}
	
	public String asString() {
		
		return this.value.asString();
	}
	
	public LocalTime asTime() {
		
		return this.value.asTime();
	}
	
	public ZoneId asTimeZone() {
		
		return this.value.asTimeZone();
	}
	
	public static PolyglotValue asValue(Object o) {
		
		return new PolyglotValue(Value.asValue(o));
	}
	
	public boolean canExecute() {
		
		return this.value.canExecute();
	}
	
	public boolean canInstantiate() {
		
		return this.value.canInstantiate();
	}
	
	public boolean canInvokeMember(String identifier) {
		
		return this.value.canInvokeMember(identifier);
	}
	
	public boolean equals(Object obj) {
		
		return this.value.equals(obj);
	}
	
	public PolyglotValue execute(Object... arguments) {
		
		return new PolyglotValue(this.value.execute(arguments));
	}
	
	public void executeVoid(Object... arguments) {
		
		this.value.executeVoid(arguments); 
	}
	
	public boolean fitsInByte() {
		
		return this.value.fitsInByte();
	}
	
	public boolean fitsInDouble() {
		
		return this.value.fitsInDouble();
	}
	
	public boolean fitsInFloat() {
		
		return this.value.fitsInFloat();
	}
	
	public boolean fitsInInt() {
		
		return this.value.fitsInInt();
	}
	
	public boolean fitsInLong() {
		
		return this.value.fitsInLong();
	}
	
	public boolean fitsInShort() {
		
		return this.value.fitsInShort();
	}
	
	public PolyglotValue getArrayElement(long index) {
		
		return new PolyglotValue(this.value.getArrayElement(index));
	}
	
	public long getArraySize() {
		
		return this.value.getArraySize();
	}
	
	public long getBufferSize() {
		
		return this.value.getBufferSize();
	}
	
	public PolyglotContext getContext() {
		
		return new PolyglotContext(this.value.getContext());
	}
	
	public PolyglotValue getHashEntriesIterator() {
		
		return new PolyglotValue(this.value.getHashEntriesIterator());
	}
	
	public PolyglotValue getHashKeysIterator() {
		
		return new PolyglotValue(this.value.getHashKeysIterator());
	}
	
	public long getHashSize() {
		
		return this.value.getHashSize();
	}
	
	public PolyglotValue getHashValue(Object key) {
		
		return new PolyglotValue(this.value.getHashValue(key));
	}
	
	public PolyglotValue getHashValueOrDefault(Object key, Object defaultValue) {
		
		return new PolyglotValue(this.value.getHashValueOrDefault(key, defaultValue));
	}
	
	public PolyglotValue getHashValuesIterator() {
		
		return new PolyglotValue(this.value.getHashValuesIterator());
	}
	
	public PolyglotValue getIterator() {
		
		return new PolyglotValue(this.value.getIterator());
	}
	
	public PolyglotValue getIteratorNextElement() {
		
		return new PolyglotValue(this.value.getIteratorNextElement());
	}
	
	public PolyglotValue getMember(String identifier) {
		
		return new PolyglotValue(this.value.getMember(identifier));
	}
	
	public Set<String> getMemberKeys() {
		
		return this.value.getMemberKeys();
	}
	
	public PolyglotValue getMetaObject() {
		
		return new PolyglotValue(this.value.getMetaObject());
	}
	
	public String getMetaQualifiedName() {
		
		return this.value.getMetaQualifiedName();
	}
	
	public String getMetaSimpleName() {
		
		return this.value.getMetaSimpleName();
	}
	
	public PolyglotSourceSection getSourceLocation() {
		
		return new PolyglotSourceSection(this.value.getSourceLocation());
	}
	
	public boolean hasArrayElements() {
		
		return this.value.hasArrayElements();
	}
	
	public boolean hasBufferElements() {
		
		return this.value.hasBufferElements();
	}
	
	public boolean hasHashEntries() {
		
		return this.value.hasHashEntries();
	}
	
	public boolean hasHashEntry(Object key) {
		
		return this.value.hasHashEntry(key);
	}
	
	@Override
	public int hashCode() {
		
		return this.value.hashCode();
	}
	
	public boolean hasIterator() {
		
		return this.value.hasIterator();
	}
	
	public boolean hasIteratorNextElement() {
		
		return this.value.hasIteratorNextElement();
	}
	
	public boolean hasMember(String identifier) {
		
		return this.value.hasMember(identifier);
	}
	
	public boolean hasMembers() {
		
		return this.value.hasMembers();
	}
	
	public PolyglotValue invokeMember(String identifier, Object... arguments) {
		
		return new PolyglotValue(this.value.invokeMember(identifier, arguments));
	}
	
	public boolean isBoolean() {
		
		return this.value.isBoolean();
	}
	
	public boolean isBufferWritable() {
		
		return this.value.isBufferWritable();
	}
	
	public boolean isDate() {
		
		return this.value.isDate();
	}
	
	public boolean isDuration() {
		
		return this.value.isDuration();
	}
	
	public boolean isException() {
		
		return this.value.isException();
	}
	
	public boolean isHostObject() {
		
		return this.value.isHostObject();
	}
	
	public boolean isInstant() {
		
		return this.value.isInstant();
	}
	
	public boolean isIterator() { 
		
		return this.value.isIterator();
	}
	
	public boolean isMetaInstance(Object instance) {
		
		return this.value.isMetaInstance(instance);
	}
	
	public boolean isMetaObject() {
		
		return this.value.isMetaObject();
	}
	
	public boolean isNativePointer() {
		
		return this.value.isNativePointer();
	}
	
	public boolean isNull() {
		
		return this.value.isNull();
	}
	
	public boolean isNumber() {
		
		return this.value.isNumber();
	}
	
	public boolean isProxyObject() {
		
		return this.value.isProxyObject();
	}
	
	public boolean isString() {
		
		return this.value.isString();
	}
	
	public boolean isTime() {
		
		return this.value.isTime();
	}
	
	public boolean isTimeZone() {
		
		return this.value.isTimeZone();
	}
	
	public PolyglotValue newInstance(Object... arguments) {
		
		return new PolyglotValue(this.value.newInstance(arguments));
	}
	
	public void pin() {
		
		this.value.pin();
	}
	
	public void putHashEntry(Object key, Object value) {
		
		this.value.putHashEntry(key, value);
	}
	
	public void putMember(String identifier, Object value) {
		
		this.value.putMember(identifier, value);
	}
	
	public byte readBufferByte(long byteOffset) {
		
		return this.value.readBufferByte(byteOffset);
	}
	
	public double readBufferDouble(ByteOrder order, long byteOffset) {
		
		return this.value.readBufferDouble(order, byteOffset);
	}
	
	public float readBufferFloat(ByteOrder order, long byteOffset) {
		
		return this.value.readBufferFloat(order, byteOffset);
	}
	
	public int readBufferInt(ByteOrder order, long byteOffset) {
		
		return this.value.readBufferInt(order, byteOffset);
	}
	
	public long readBufferLong(ByteOrder order, long byteOffset) {
		
		return this.value.readBufferLong(order, byteOffset);
	}
	
	public short readBufferShort(ByteOrder order, long byteOffset) {
		
		return this.value.readBufferShort(order, byteOffset);
	}
	
	public boolean removeArrayElement(long index) {
		
		return this.value.removeArrayElement(index);
	}
	
	public boolean removeHashEntry(Object key) {
		
		return this.value.removeHashEntry(key);
	}
	
	public boolean removeMember(String identifier) {
		
		return this.value.removeMember(identifier);
	}
	
	public void setArrayElement(long index, Object value) {
		
		this.value.setArrayElement(index, value);
	}
	
	public RuntimeException throwException() {
		
		return this.value.throwException();
	}
	
	@Override
	public String toString() {
		
		return this.value.toString();
	}
	
	public void writeBufferByte(long byteOffset, byte value) {
		
		this.value.writeBufferByte(byteOffset, value);
	}
	
	public void writeBufferDouble(ByteOrder order, long byteOffset, double value) {
		
		this.value.writeBufferDouble(order, byteOffset, value);
	}
	
	public void writeBufferFloat(ByteOrder order, long byteOffset, float value) {
		
		this.value.writeBufferFloat(order, byteOffset, value);
	}
	
	public void writeBufferInt(ByteOrder order, long byteOffset, int value) {
		
		this.value.writeBufferInt(order, byteOffset, value);
	}
	
	public void writeBufferLong(ByteOrder order, long byteOffset, long value) {
		
		this.value.writeBufferLong(order, byteOffset, value);
	}
	
	public void writeBufferShort(ByteOrder order, long byteOffset, short value) {
		
		this.value.writeBufferShort(order, byteOffset, value);
	}
	
}
