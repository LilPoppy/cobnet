package com.storechain.spring.boot.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("system")
public class SystemConfiguration {
	

	private TaskProviderConfiguration taskProvider; 
	

	@Configuration
	@ConfigurationProperties
	public static class TaskProviderConfiguration {
	
		private int threadCount = 8;
		
		private int maxThreadCount = Integer.MAX_VALUE;
		
		private int keepAliveMinutes = 5;
		
		private boolean allowCoreThreadTimeOut = true;
		
		private boolean removeOnCancelPolicy = true;
		
		private boolean continueExistingPeriodicTasksAfterShutdownPolicy;
		
		private boolean executeExistingDelayedTasksAfterShutdownPolicy;

		public int getThreadCount() {
			return threadCount;
		}

		public void setThreadCount(int threadCount) {
			this.threadCount = threadCount;
		}

		public int getKeepAliveMinutes() {
			return keepAliveMinutes;
		}

		public void setKeepAliveMinutes(int keepAliveMinutes) {
			this.keepAliveMinutes = keepAliveMinutes;
		}

		public boolean isAllowCoreThreadTimeOut() {
			return allowCoreThreadTimeOut;
		}

		public void setAllowCoreThreadTimeOut(boolean allowCoreThreadTimeOut) {
			this.allowCoreThreadTimeOut = allowCoreThreadTimeOut;
		}

		public boolean isRemoveOnCancelPolicy() {
			return removeOnCancelPolicy;
		}

		public void setRemoveOnCancelPolicy(boolean removeOnCancelPolicy) {
			this.removeOnCancelPolicy = removeOnCancelPolicy;
		}

		public boolean isContinueExistingPeriodicTasksAfterShutdownPolicy() {
			return continueExistingPeriodicTasksAfterShutdownPolicy;
		}

		public void setContinueExistingPeriodicTasksAfterShutdownPolicy(
				boolean continueExistingPeriodicTasksAfterShutdownPolicy) {
			this.continueExistingPeriodicTasksAfterShutdownPolicy = continueExistingPeriodicTasksAfterShutdownPolicy;
		}

		public boolean isExecuteExistingDelayedTasksAfterShutdownPolicy() {
			return executeExistingDelayedTasksAfterShutdownPolicy;
		}

		public void setExecuteExistingDelayedTasksAfterShutdownPolicy(
				boolean executeExistingDelayedTasksAfterShutdownPolicy) {
			this.executeExistingDelayedTasksAfterShutdownPolicy = executeExistingDelayedTasksAfterShutdownPolicy;
		}

		public int getMaxThreadCount() {
			return this.maxThreadCount < this.threadCount ? Integer.MAX_VALUE : this.maxThreadCount;
		}

		public void setMaxThreadCount(int maxThreadCount) {
			this.maxThreadCount = maxThreadCount;
		}
	}

	public TaskProviderConfiguration getTaskProvider() {
		return taskProvider;
	}


	public void setTaskProvider(TaskProviderConfiguration timerProvider) {
		this.taskProvider = timerProvider;
	}

}
