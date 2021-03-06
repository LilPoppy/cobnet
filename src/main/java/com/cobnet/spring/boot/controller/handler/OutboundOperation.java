package com.cobnet.spring.boot.controller.handler;

import java.util.Arrays;

public enum OutboundOperation {
	
	UNKNOWN(0),
	AUTHENTICATION(1),
	CONNECT(55);
	
	private final long opcode;
	
	private OutboundOperation(long opcode) {

		this.opcode = opcode;
	}
	
	public long code() {

		return this.opcode;
	}
	
	public static OutboundOperation getByCode(long opcode) {

		return Arrays.stream(OutboundOperation.values()).filter(header -> header.opcode == opcode).findFirst().orElse(OutboundOperation.UNKNOWN);
	}
 }
