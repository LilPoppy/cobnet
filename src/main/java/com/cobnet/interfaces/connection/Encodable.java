package com.cobnet.interfaces.connection;

import com.cobnet.connection.OutboundPacket;

public interface Encodable {
	
    void encode(OutboundPacket outPacket);
}
