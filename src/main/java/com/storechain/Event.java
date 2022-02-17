package com.storechain;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.jetty.util.ConcurrentHashSet;

import com.storechain.interfaces.EventHandler;

public class Event {

	private final Set<EventHandler> handlers = ConcurrentHashMap.newKeySet();
	
	public Event(EventHandler... handlers) {
		
		this.handlers.addAll(Arrays.asList(handlers));
	}
	
	public void addHandler(EventHandler handler) {
		
		handlers.add(handler);
	}
	
	public void removeHandler(EventHandler handler) {
		
		handlers.remove(handler);
	}
	
	public void clearHandlers() {
		
		handlers.clear();
	}
	
	public Set<EventHandler> registeredHandlers() {
		
		return Collections.unmodifiableSet(handlers);
	}
	
	public void notifyHandlers(Object... args) {
		
		for(EventHandler handler : handlers) {
			
			handler.notify(this, args);
		}
	}
}
