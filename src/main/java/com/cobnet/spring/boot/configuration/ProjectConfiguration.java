package com.cobnet.spring.boot.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("project")
public class ProjectConfiguration {

	private InformationConfiguration information;

	private TaskProviderConfiguration taskProvider;

	public InformationConfiguration getInformation() {
		return information;
	}

	public void setInformation(InformationConfiguration information) {
		this.information = information;
	}

	public TaskProviderConfiguration getTaskProvider() {
		return taskProvider;
	}

	public void setTaskProvider(TaskProviderConfiguration timerProvider) {
		this.taskProvider = timerProvider;
	}

	public static class InformationConfiguration {

		private String groupId;

		private String artifactId;

		private String version;

		private String name;

		private String description;

		public String getGroupId() {
			return groupId;
		}

		public void setGroupId(String groupId) {
			this.groupId = groupId;
		}

		public String getArtifactId() {
			return artifactId;
		}

		public void setArtifactId(String artifactId) {
			this.artifactId = artifactId;
		}

		public String getVersion() {
			return version;
		}

		public void setVersion(String version) {
			this.version = version;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}
	}

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

}
