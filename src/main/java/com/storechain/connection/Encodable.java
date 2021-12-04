package com.storechain.connection;

public interface Encodable {
	
    void encode(OutboundPacket outPacket);
}
