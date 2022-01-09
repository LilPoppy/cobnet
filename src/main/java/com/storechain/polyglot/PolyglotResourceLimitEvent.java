package com.storechain.polyglot;

import org.graalvm.polyglot.ResourceLimitEvent;

public class PolyglotResourceLimitEvent {

	private final ResourceLimitEvent event;
	
	public PolyglotResourceLimitEvent(ResourceLimitEvent event) {
		
		this.event = event;
	}

	public ResourceLimitEvent getEvent() {
		
		return event;
	}
	
	public PolyglotContext getContext() {
		
		return new PolyglotContext(this.event.getContext());
	}
	
	@Override
	public String toString() {
		
		return this.event.toString();
	}
}
