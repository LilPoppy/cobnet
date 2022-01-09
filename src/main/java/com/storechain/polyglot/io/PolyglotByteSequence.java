package com.storechain.polyglot.io;

import java.util.stream.IntStream;

import org.graalvm.polyglot.io.ByteSequence;

public class PolyglotByteSequence {
	
	private final ByteSequence sequence;
	
	public PolyglotByteSequence(ByteSequence sequence) {
		
		this.sequence = sequence;
	}

	public ByteSequence getSequence() {
		
		return sequence;
	}

	public byte byteAt(int index) {
		
		return this.sequence.byteAt(index);
	}
	
	public IntStream bytes() {
		
		return this.sequence.bytes();
	}
	
	public static PolyglotByteSequence create(byte[] buffer) {
		
		return new PolyglotByteSequence(ByteSequence.create(buffer));
	}
	
	public int length() {
		
		return this.sequence.length();
	}
	
	public PolyglotByteSequence subSequence(int startIndex, int endIndex) {
		
		return new PolyglotByteSequence(this.sequence.subSequence(startIndex, endIndex));
	}
	
	public byte[] toByteArray() {
		
		return this.sequence.toByteArray();
	}
}
