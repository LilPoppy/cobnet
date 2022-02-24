package com.storechain.utils;

import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;

import com.storechain.EntryPoint;

public class SpringManager {
	
	public static ServletWebServerApplicationContext getContext() {
		
		return EntryPoint.CONTEXT;
	}
}
