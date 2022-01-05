package com.storechain.utils;

import java.util.Date;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import com.storechain.EntryPoint;

public class TaskProvider  {
	
	public static final TaskProvider INSTANCE = new TaskProvider();
	
	private final ScheduledThreadPoolExecutor executor;
	
	private TaskProvider() {
		
		this.executor = new ScheduledThreadPoolExecutor(EntryPoint.SYSTEM_CONFIG.getTaskProvider().getThreadCount(), new ThreadFactory() {
        	
            private final AtomicInteger number = new AtomicInteger(-1);

            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r);
                t.setName("TimerProvider:" + number.getAndIncrement());
                return t;
            }
        });
		
		executor.setMaximumPoolSize(EntryPoint.SYSTEM_CONFIG.getTaskProvider().getMaxThreadCount());
        executor.setContinueExistingPeriodicTasksAfterShutdownPolicy(EntryPoint.SYSTEM_CONFIG.getTaskProvider().isContinueExistingPeriodicTasksAfterShutdownPolicy());
        executor.setExecuteExistingDelayedTasksAfterShutdownPolicy(EntryPoint.SYSTEM_CONFIG.getTaskProvider().isExecuteExistingDelayedTasksAfterShutdownPolicy());
        executor.setRemoveOnCancelPolicy(EntryPoint.SYSTEM_CONFIG.getTaskProvider().isRemoveOnCancelPolicy());
        executor.setKeepAliveTime(EntryPoint.SYSTEM_CONFIG.getTaskProvider().getKeepAliveMinutes(), TimeUnit.MINUTES);
        executor.allowCoreThreadTimeOut(EntryPoint.SYSTEM_CONFIG.getTaskProvider().isAllowCoreThreadTimeOut());
	}
	
	public ScheduledFuture<?> register(Runnable delegate, long delay, long period, TimeUnit unit, Function<Object[], Boolean> continuous, Object...args) {
		
		TaskScheduleRunnable runnable = new TaskScheduleRunnable(delegate, continuous, args);
		
		runnable.self = this.executor.scheduleAtFixedRate(runnable, delay, period, unit);
		
		return runnable.self;
		
	}
	
	public ScheduledFuture<?> register(Runnable delegate, long delay, long period, TimeUnit unit, final int maxCount, Runnable onDone, Object... args) {
		
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
	
	public ScheduledFuture<?> register(Runnable delegate, long period, TimeUnit unit, int maxCount, Runnable onDone, Object... args) {
		return register(delegate, 0, period, unit , maxCount, onDone, args);
	}
	
	public ScheduledFuture<?> register(Runnable delegate, long milliSeconds, int maxCount, Runnable onDone, Object... args) {
		return register(delegate, milliSeconds, TimeUnit.MICROSECONDS, maxCount, onDone, args);
	}
	
	public ScheduledFuture<?> register(Runnable delegate, long period, TimeUnit unit, int maxCount, Object... args) {
		return register(delegate, period, unit, maxCount, null, args);
	}
	
	public ScheduledFuture<?> register(Runnable delegate, long milliSeconds, int maxCount, Object... args) {
		return register(delegate, milliSeconds, TimeUnit.MILLISECONDS, maxCount, args);
	}
	
	public ScheduledFuture<?> register(Runnable delegate, long milliSeconds) {
		return register(delegate, milliSeconds, -1);
	}
	
	public ScheduledFuture<?> register(Runnable delegate, long period, TimeUnit unit) {
		return register(delegate, period, unit, -1);
	}
	
	public ScheduledFuture<?> register(Runnable delegate, long delay, long period, TimeUnit unit) {
		return register(delegate, delay, period, unit, -1);
	}
	
	
	public ScheduledFuture<?> register(Runnable delegate, long delay, long period, TimeUnit unit, int maxCount, Object... args) {
		return register(delegate, delay, period, unit, maxCount, null, args);
	}
	
	public ScheduledFuture<?> register(Runnable delegate, Date delay, long milliSeconds, int maxCount, Object... args) {
		long v = delay.getTime() - System.currentTimeMillis();
		return register(delegate, v > 0 ? v : 0, milliSeconds, TimeUnit.MILLISECONDS, maxCount, args);
	}
	
	public ScheduledFuture<?> register(Runnable delegate, Date delay, long milliSeconds) {
		long v = delay.getTime() - System.currentTimeMillis();
		return register(delegate, v > 0 ? v : 0, milliSeconds, TimeUnit.MILLISECONDS);
	}
	
	public ScheduledFuture<?> schedule(Runnable delegate, long delay, TimeUnit unit, Object... args) {
		
		TaskScheduleRunnable runnable = new TaskScheduleRunnable(delegate, args);
		
		runnable.self = this.executor.schedule(runnable, delay, unit);
		
		return runnable.self;
	}
	
	public ScheduledFuture<?> schedule(Runnable delegate, long milliSeconds, Object... args) {
		return schedule(delegate, milliSeconds, TimeUnit.MILLISECONDS, args);
	}
	
	public ScheduledFuture<?> schedule(Runnable delegate, Date date, Object... args) {
		long delay = date.getTime() - System.currentTimeMillis();
		return schedule(delegate, delay > 0 ? delay : 0, TimeUnit.MILLISECONDS, args);
	}
	
	public void purge() {
		this.executor.purge();
	}
	
    public long getActiveCount() {
        return this.executor.getActiveCount();
    }

    public long getCompletedTaskCount() {
        return this.executor.getCompletedTaskCount();
    }
    
    public long getTaskCount() {
        return this.executor.getTaskCount();
    }

    public boolean isShutdown() {
        return this.executor.isShutdown();
    }
	
    public boolean isTerminated() {
        return this.executor.isTerminated();
    }
    
    public int getPoolSize() {
    	return this.executor.getPoolSize();
    }
    
    public int getQueueCount() {
    	return this.getQueue().size();
    }
    
	public BlockingQueue<Runnable> getQueue() {
		return this.executor.getQueue();
	}
	
	public List<Runnable> shutdownNow() {
		return this.executor.shutdownNow();
	}
	
	public void shutdown() {
		this.executor.shutdown();
	}
	
	public ScheduledThreadPoolExecutor getExecutor() {
		return executor;
	}
	
	@Override
	public String toString() {
		return this.executor.toString();
	}

	private class TaskScheduleRunnable implements Runnable {

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
