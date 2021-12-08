package com.storechain.spring.boot.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("system")
public class SystemConfiguration {
	
	private int timerThreadCount = 8;

	public int getTimerThreadCount() {
		return timerThreadCount;
	}

	public void setTimerThreadCount(int timerThreadCount) {
		this.timerThreadCount = timerThreadCount;
	}
}
