package com.cobnet.polyglot;

import com.cobnet.spring.boot.core.ProjectBeanHolder;
import org.graalvm.polyglot.Context;
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

		Context context = this.event.getContext();

		return ProjectBeanHolder.getScriptEngineManager().stream().filter(managed -> managed.equals(context)).findFirst().orElse(new PolyglotContext("unmanaged", context));
	}
	
	@Override
	public String toString() {
		
		return this.event.toString();
	}
}
