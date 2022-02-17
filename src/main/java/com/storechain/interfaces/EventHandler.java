package com.storechain.interfaces;

import com.storechain.Event;

public interface EventHandler {

	public void notify(Event event, Object... args);
}
