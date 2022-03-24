package com.cobnet.interfaces.connection;

import com.cobnet.connection.support.OutboundPacket;

public interface Encodable {
	
    void encode(OutboundPacket outPacket);
}
