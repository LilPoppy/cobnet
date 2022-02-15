package com.storechain.interfaces;

import com.storechain.utils.Event;

public interface EventHandler {

	public void notify(Event event, Object... args);
}
