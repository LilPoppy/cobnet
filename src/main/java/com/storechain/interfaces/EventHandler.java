package com.storechain.interfaces;

import com.storechain.common.Event;

public interface EventHandler {

	public void notify(Event event, Object... args);
}
