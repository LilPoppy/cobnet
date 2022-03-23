package com.cobnet.spring.boot.controller.handler;

import java.util.Arrays;

public enum InboundOperation {

	UNKNOWN(0),
	AUTHENTICATION(1),
	RECONNECT(5);
	
	private final long opcode;
	
	private InboundOperation(long opcode) {

		this.opcode = opcode;
	}
	
	public long code() {

		return this.opcode;
	}
	
	public static InboundOperation getByCode(long code) {

		return Arrays.stream(InboundOperation.values()).filter(header -> header.opcode == code).findFirst().orElse(InboundOperation.UNKNOWN);
	}
}
