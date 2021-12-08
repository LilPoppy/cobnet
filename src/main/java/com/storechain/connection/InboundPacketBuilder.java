package com.storechain.connection;

import java.nio.charset.Charset;

import com.storechain.Endian;
import com.storechain.interfaces.connection.PacketBuilder;

public class InboundPacketBuilder implements PacketBuilder<InboundPacket> {

	@Override
	public InboundPacket build(byte[] buf, Endian endian, Charset charset) {
		InboundPacket packet = new InboundPacket(buf, endian, charset);
		packet.decodeHeader();
		return packet;
	}



}
