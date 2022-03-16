package com.cobnet.spring.boot.core;

import com.cobnet.spring.boot.configuration.ProjectConfiguration;

import java.util.Date;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

public class TaskProvider  {

	private final ScheduledThreadPoolExecutor executor;
	
	TaskProvider(ProjectConfiguration.TaskProviderConfiguration config) {
		
		this.executor = new ScheduledThreadPoolExecutor(config.getThreadCount(), new ThreadFactory() {
        	
            private final AtomicInteger number = new AtomicInteger(-1);

            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r);
                t.setName("TaskProvider:" + number.getAndIncrement());
                return t;
            }
        });
		
		executor.setMaximumPoolSize(config.getMaxThreadCount());
        executor.setContinueExistingPeriodicTasksAfterShutdownPolicy(config.isContinueExistingPeriodicTasksAfterShutdownPolicy());
        executor.setExecuteExistingDelayedTasksAfterShutdownPolicy(config.isExecuteExistingDelayedTasksAfterShutdownPolicy());
        executor.setRemoveOnCancelPolicy(config.isRemoveOnCancelPolicy());
        executor.setKeepAliveTime(config.getKeepAliveMinutes(), TimeUnit.MINUTES);
        executor.allowCoreThreadTimeOut(config.isAllowCoreThreadTimeOut());
	}
	
	public static ScheduledFuture<?> register(Runnable delegate, long delay, long period, TimeUnit unit, Function<Object[], Boolean> continuous, Object...args) {
		
		TaskScheduleRunnable runnable = new TaskScheduleRunnable(delegate, continuous, args);
		
		runnable.self = ProjectBeanHolder.getTaskProvider().executor.scheduleAtFixedRate(runnable, delay, period, unit);
		
		return runnable.self;
		
	}
	
	public static ScheduledFuture<?> register(Runnable delegate, long delay, long period, TimeUnit unit, final int maxCount, Runnable onDone, Object... args) {
		
		return register(delegate, delay, period, unit, new Function<Object[], Boolean>() {
			
			private final AtomicInteger count = new AtomicInteger();
			
			@Override
			public Boolean apply(Object[] t) {

				if(maxCount > 0 && count.incrementAndGet() >= maxCount) {
					
					if(onDone != null) {
						
						onDone.run();
					}
					
					return false;
				}
				
				return true;
			}
			
		}, args);
	}
	
	public static ScheduledFuture<?> register(Runnable delegate, long period, TimeUnit unit, int maxCount, Runnable onDone, Object... args) {
		return register(delegate, 0, period, unit , maxCount, onDone, args);
	}
	
	public static ScheduledFuture<?> register(Runnable delegate, long milliSeconds, int maxCount, Runnable onDone, Object... args) {
		return register(delegate, milliSeconds, TimeUnit.MICROSECONDS, maxCount, onDone, args);
	}
	
	public static ScheduledFuture<?> register(Runnable delegate, long period, TimeUnit unit, int maxCount, Object... args) {
		return register(delegate, period, unit, maxCount, null, args);
	}
	
	public static ScheduledFuture<?> register(Runnable delegate, long milliSeconds, int maxCount, Object... args) {
		return register(delegate, milliSeconds, TimeUnit.MILLISECONDS, maxCount, args);
	}
	
	public static ScheduledFuture<?> register(Runnable delegate, long milliSeconds) {
		return register(delegate, milliSeconds, -1);
	}
	
	public static ScheduledFuture<?> register(Runnable delegate, long period, TimeUnit unit) {
		return register(delegate, period, unit, -1);
	}
	
	public static ScheduledFuture<?> register(Runnable delegate, long delay, long period, TimeUnit unit) {
		return register(delegate, delay, period, unit, -1);
	}
	
	
	public static ScheduledFuture<?> register(Runnable delegate, long delay, long period, TimeUnit unit, int maxCount, Object... args) {
		return register(delegate, delay, period, unit, maxCount, null, args);
	}
	
	public static ScheduledFuture<?> register(Runnable delegate, Date delay, long milliSeconds, int maxCount, Object... args) {
		long v = delay.getTime() - System.currentTimeMillis();
		return register(delegate, v > 0 ? v : 0, milliSeconds, TimeUnit.MILLISECONDS, maxCount, args);
	}
	
	public static ScheduledFuture<?> register(Runnable delegate, Date delay, long milliSeconds) {
		long v = delay.getTime() - System.currentTimeMillis();
		return register(delegate, v > 0 ? v : 0, milliSeconds, TimeUnit.MILLISECONDS);
	}
	
	public static ScheduledFuture<?> schedule(Runnable delegate, long delay, TimeUnit unit, Object... args) {
		
		TaskScheduleRunnable runnable = new TaskScheduleRunnable(delegate, args);
		
		runnable.self = ProjectBeanHolder.getTaskProvider().executor.schedule(runnable, delay, unit);
		
		return runnable.self;
	}
	
	public static ScheduledFuture<?> schedule(Runnable delegate, long milliSeconds, Object... args) {
		return schedule(delegate, milliSeconds, TimeUnit.MILLISECONDS, args);
	}
	
	public static ScheduledFuture<?> schedule(Runnable delegate, Date date, Object... args) {
		long delay = date.getTime() - System.currentTimeMillis();
		return schedule(delegate, delay > 0 ? delay : 0, TimeUnit.MILLISECONDS, args);
	}
	
	public static void purge() {
		ProjectBeanHolder.getTaskProvider().executor.purge();
	}
	
    public static long getActiveCount() {
        return ProjectBeanHolder.getTaskProvider().executor.getActiveCount();
    }

    public static long getCompletedTaskCount() {
        return ProjectBeanHolder.getTaskProvider().executor.getCompletedTaskCount();
    }
    
    public static long getTaskCount() {
        return ProjectBeanHolder.getTaskProvider().executor.getTaskCount();
    }

    public static boolean isShutdown() {
        return ProjectBeanHolder.getTaskProvider().executor.isShutdown();
    }
	
    public static boolean isTerminated() {
        return ProjectBeanHolder.getTaskProvider().executor.isTerminated();
    }
    
    public static int getPoolSize() {
    	return ProjectBeanHolder.getTaskProvider().executor.getPoolSize();
    }
    
    public static int getQueueCount() {
    	return TaskProvider.getQueue().size();
    }
    
	public static BlockingQueue<Runnable> getQueue() {

		return ProjectBeanHolder.getTaskProvider().executor.getQueue();
	}
	
	public static List<Runnable> shutdownNow() {
		return ProjectBeanHolder.getTaskProvider().executor.shutdownNow();
	}
	
	public static void shutdown() {
		ProjectBeanHolder.getTaskProvider().executor.shutdown();
	}
	
	public static ScheduledThreadPoolExecutor getExecutor() {
		return ProjectBeanHolder.getTaskProvider().executor;
	}
	
	@Override
	public String toString() {
		return this.executor.toString();
	}

	private static class TaskScheduleRunnable implements Runnable {

		private volatile ScheduledFuture<?> self;
		private final Runnable delegate;
		private final Function<Object[], Boolean> continuous;
		private final Object[] args;
		
		public TaskScheduleRunnable(Runnable delegate, Function<Object[], Boolean> continuous, Object... args) {
			this.delegate = delegate;
			this.continuous = continuous;
			this.args = args;
		}
		
		public TaskScheduleRunnable(Runnable delegate, Object... args) {
			this(delegate, null, args);
		}
		

		@Override
		public void run() {
						
			this.delegate.run();
			
			if(this.continuous != null) {
				
				if(!this.continuous.apply(this.args)) {
					
					this.self.cancel(false);
				}
			}
		}
	}
}
