package com.storechain.polyglot.io;

import java.io.IOException;
import java.net.URI;

import org.graalvm.polyglot.io.MessageTransport;
import org.graalvm.polyglot.io.MessageTransport.VetoException;
import org.graalvm.polyglot.io.MessageEndpoint;

public class PolyglotMessageTransport {
	
	private final MessageTransport transport;
	
	public PolyglotMessageTransport(MessageTransport transport) {
		
		this.transport = transport;
	}

	public MessageTransport getTransport() {
		return transport;
	}
	
	public PolyglotMessageEndpoint open(URI uri, MessageEndpoint peerEndpoint) throws IOException, VetoException {
		
		return new PolyglotMessageEndpoint(this.transport.open(uri, peerEndpoint));
	}
	
	
	public PolyglotMessageEndpoint open(URI uri, PolyglotMessageEndpoint peerEndpoint) throws IOException, VetoException {
		
		return this.open(uri, peerEndpoint.getEndpoint());
	}
}
