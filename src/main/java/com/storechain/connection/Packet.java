package com.storechain.connection;

import java.nio.charset.Charset;

import com.google.common.io.BaseEncoding;
import com.storechain.common.Endian;

import io.netty.buffer.ByteBuf;
import reactor.core.Disposable;

public abstract class Packet implements Cloneable, Disposable {

	private byte[] data;
	
	protected final Endian endian;
	
	protected final Charset charset;

	public Packet(byte[] data, Endian endian, Charset charset) {
        this.data = new byte[data.length];
        System.arraycopy(data, 0, this.data, 0, data.length);
		this.endian = endian;
		this.charset = charset;
	}

	public Packet(ByteBuf data, Endian endian, Charset charset) {
		this.data = new byte[data.readableBytes()];
		data.getBytes(data.readerIndex(), this.data);
		this.endian = endian;
		this.charset = charset;
	}

	public int getLength() {
		if (data != null) {
			return data.length;
		}
		return 0;
	}
	
	public void setData(byte[] data) {
		this.data = data;
	}

	public byte[] getData() {
		return data;
	}
	

	public abstract ByteBuf getBuf();
	
	@Override
	public abstract Packet clone() throws CloneNotSupportedException;
	
	@Override
	public String toString() {
		return BaseEncoding.base16().withSeparator(" ", 2).upperCase().encode(getData());
	}
	
}
