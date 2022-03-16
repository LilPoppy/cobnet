package com.cobnet.polyglot.io;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.graalvm.polyglot.io.MessageEndpoint;

public class PolyglotMessageEndpoint {

	private final MessageEndpoint endpoint;
	
	public PolyglotMessageEndpoint(MessageEndpoint endpoint) {
		
		this.endpoint = endpoint;
	}

	public MessageEndpoint getEndpoint() {
		
		return endpoint;
	}
	
	public void sendBinary(ByteBuffer data) throws IOException {
		
		this.endpoint.sendBinary(data);
	}
	
	public void sendClose() throws IOException {
		
		this.endpoint.sendClose();
	}
	
	public void	sendPing(ByteBuffer data) throws IOException {
		
		this.endpoint.sendPing(data);
	}
	
	public void sendPong(ByteBuffer data) throws IOException {
		
		this.endpoint.sendPong(data);
	}
	
	public void	sendText(String text) throws IOException {
		
		this.endpoint.sendText(text);
	}
}
