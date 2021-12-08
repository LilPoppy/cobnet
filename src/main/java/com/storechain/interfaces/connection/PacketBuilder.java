package com.storechain.interfaces.connection;

import java.nio.charset.Charset;

import com.storechain.Endian;
import com.storechain.connection.Packet;

public interface PacketBuilder<T extends Packet> {

	T build(byte[] buf, Endian endian, Charset charset);
}
