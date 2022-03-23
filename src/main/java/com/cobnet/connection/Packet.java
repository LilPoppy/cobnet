package com.cobnet.connection;

import java.nio.charset.Charset;

import com.cobnet.common.Endian;
import com.cobnet.interfaces.connection.Transmission;
import com.google.common.io.BaseEncoding;

import io.netty.buffer.ByteBuf;
import reactor.core.Disposable;

public abstract class Packet implements Transmission<byte[]>, Cloneable, Disposable {

	private byte[] data;

	private NettyServer<?> server;

	public Packet(ByteBuf data, NettyServer<?> server) {

		this(data.array(), server);
	}

	public Packet(byte[] data, NettyServer<?> server) {

        this.data = new byte[data.length];
        System.arraycopy(data, 0, this.data, 0, data.length);
		this.server = server;
	}

	public NettyServer<?> getServer() {

		return this.server;
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

	@Override
	public byte[] getData() {

		return this.data;
	}

	public abstract ByteBuf getBuf();
	
	@Override
	public abstract Packet clone() throws CloneNotSupportedException;
	
	@Override
	public String toString() {

		return BaseEncoding.base16().withSeparator(" ", 2).upperCase().encode(getData());
	}
	
}
