package com.storechain.interfaces.connection;

import com.storechain.connection.OutboundPacket;

public interface Encodable {
	
    void encode(OutboundPacket outPacket);
}
