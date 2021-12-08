package com.storechain.utils;

import java.util.Date;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.storechain.EntryPoint;

public class TimerProvider {
	
	public static final TimerProvider INSTANCE = new TimerProvider();
	
	private final ScheduledThreadPoolExecutor executor;
	
	public TimerProvider() {
		
		this.executor = new ScheduledThreadPoolExecutor(EntryPoint.SYSTEM_CONFIG.getTimerThreadCount());
	}
	
	public ScheduledFuture<?> register(Runnable delegate, long delay, long period, TimeUnit unit, int maxCount, Runnable onDone, Object... args) {
		return this.executor.scheduleAtFixedRate(new TimerScheduleRunnable(delegate, maxCount, onDone, args), delay, period, unit);
	}
	
	public ScheduledFuture<?> register(Runnable delegate, long period, TimeUnit unit, int maxCount, Runnable onDone, Object... args) {
		return register(delegate, 0, period, unit , maxCount, onDone, args);
	}
	
	public ScheduledFuture<?> register(Runnable delegate, long milliSeconds, int maxCount, Runnable onDone, Object... args) {
		return register(delegate, milliSeconds, TimeUnit.MICROSECONDS, maxCount, onDone, args);
	}
	
	public ScheduledFuture<?> register(Runnable delegate, long period, TimeUnit unit, int maxCount, Object... args) {
		return register(delegate,  period, unit, maxCount, null, args);
	}
	
	public ScheduledFuture<?> register(Runnable delegate, long milliSeconds, Object... args) {
		return register(delegate,  milliSeconds, TimeUnit.MILLISECONDS, -1, null, args);
	}
	
	public ScheduledFuture<?> register(Runnable delegate, long period, TimeUnit unit, Object... args) {
		return register(delegate, period, unit, -1, null, args);
	}
	
	public ScheduledFuture<?> register(Runnable delegate, long delay, long period, TimeUnit unit, int maxCount, Object... args) {
		return register(delegate, delay, period, unit, maxCount, null, args);
	}
	
	public ScheduledFuture<?> register(Runnable delegate, long delay, long period, TimeUnit unit, Object... args) {
		return register(delegate, delay, period, unit, -1, null, args);
	}
	
	public ScheduledFuture<?> schedule(Runnable delegate, long delay, TimeUnit unit, Object... args) {
		return this.executor.schedule(new TimerScheduleRunnable(delegate, args), delay, unit);
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

	private class TimerScheduleRunnable extends TimerTask {

		private volatile ScheduledFuture<?> self;
		private final Runnable delegate;
		private final int maxCount;
		private final AtomicInteger count = new AtomicInteger();;
		private final Runnable onDone;
		
		public TimerScheduleRunnable(Runnable delegate, int maxCount, Runnable onDone, Object... args) {
			this.delegate = delegate;
			this.maxCount = maxCount;
			this.onDone = onDone;
		}
		
		public TimerScheduleRunnable(Runnable delegate, int maxCount, Object... args) {
			this(delegate, maxCount, null, args);
		}
		
		public TimerScheduleRunnable(Runnable delegate, Object... args) {
			this(delegate, -1, args);
		}

		@Override
		public void run() {
			
			this.delegate.run();
			
			boolean interrupted = false;
			
			if(maxCount > -1 && this.count.incrementAndGet() == maxCount) {
				
				if(this.onDone != null) {
					
					this.onDone.run();
				}
				
				try {
					
					while(self == null) {
						
	                    try {
	                        Thread.sleep(1);
	                    } catch (InterruptedException e) {
	                        interrupted = true;
	                    }
						
					}
					
					self.cancel(false);
					
				}finally {
					if(interrupted) {
						Thread.currentThread().interrupt();
					}
				}
				
			}
			
			
		}
	}
}
