package com.storechain.polyglot;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.time.Duration;
import java.time.ZoneId;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.util.function.Predicate;
import java.util.logging.Handler;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Context.Builder;
import org.graalvm.polyglot.EnvironmentAccess;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.io.FileSystem;
import org.graalvm.polyglot.io.ProcessHandler;

import com.storechain.interfaces.EventHandler;
import com.storechain.polyglot.io.PolyglotFileSystem;
import com.storechain.polyglot.io.PolyglotMessageTransport;
import com.storechain.polyglot.io.PolyglotProcessHandler;
import com.storechain.utils.Event;

import org.graalvm.polyglot.ResourceLimits;
import org.graalvm.polyglot.io.MessageTransport;

public final class PolyglotContext implements AutoCloseable {
	
	private final Context context;
	
	public final PolyglotContextCloseEvent closeEvent = new PolyglotContextCloseEvent();
	
	public PolyglotContext(String... permittedLanguages) {
		
		if(permittedLanguages.length > 0) {
			
			this.context = Context.create(permittedLanguages);
			
		} else {
			
			this.context = Context.create();
		}
	}
	
	public PolyglotContext(Context context) {
		
		this.context = context;
	}
	
	public Context getContext() {
		return this.context;
	}

    public void close(boolean cancelIfExecuting) {
    	
    	closeEvent.notifyHandlers(cancelIfExecuting);
    	
    	this.context.close(cancelIfExecuting);
    }
    
	@Override
	public void close() {

		this.closeEvent.notifyHandlers();
		
		this.context.close();
	}
	
	public static PolyglotContext create(String... permittedLanguages) {
		
		return new PolyglotContext(permittedLanguages);
	}
	
	public void enter() {
		
		this.context.enter();
	}
	
	@Override
	public boolean equals(Object obj) {
		
		return this.context.equals(obj);
	}
	
	public PolyglotValue eval(Source source) {
		
		return new PolyglotValue(this.context.eval(source));
	}
	
	public PolyglotValue eval(PolyglotSource source) {

		return this.eval(source.getSource());
	}
	
	public PolyglotValue eval(String languageId, CharSequence source) {
		
		return new PolyglotValue(this.context.eval(languageId, source));
	}
	
	public PolyglotValue getBindings(String languageId) {
		
		return new PolyglotValue(this.context.getBindings(languageId));
	}
	
	public static PolyglotContext getCurrent() {
		
		return new PolyglotContext(Context.getCurrent());
	}
	
	public PolyglotEngine getEngine() {
		
		return new PolyglotEngine(this.context.getEngine());
	}
	
	public PolyglotValue getPolyglotBindings() {
		
		return new PolyglotValue(this.context.getPolyglotBindings());
	}
	
	@Override
	public int hashCode() {
		return this.context.hashCode();
	}
	
	public boolean initialize(String languageId) {
		
		return this.context.initialize(languageId);
	}
	
	public void interrupt(Duration timeout) throws TimeoutException {
		
		this.context.interrupt(timeout);
	}
	
	public void leave() {
		
		this.context.leave();
	}
	
	public static PolyglotContextBuilder newBuilder(String... permittedLanguages) {
		
		return new PolyglotContextBuilder(Context.newBuilder(permittedLanguages));
	}
	
	public PolyglotValue parse(Source source) { 
		
		return new PolyglotValue(this.context.parse(source));
	}
	
	public PolyglotValue parse(PolyglotSource source) {
		
		return this.parse(source.getSource());
	}
	
	public PolyglotValue parse(String languageId, CharSequence source) {
		
		return new PolyglotValue(this.context.parse(languageId, source));
	}
	
	public void resetLimits() {
		
		this.context.resetLimits();
	}
	
	public void safepoint() {
		
		this.context.safepoint();
	}
	
	public static final class PolyglotContextBuilder {
		
		private Builder builder;
		
		public PolyglotContextBuilder(Builder builder) {
			
			this.builder = builder;
		}
		
		public Builder getBuilder() {
			
			return this.builder;
		}
		
		public PolyglotContextBuilder allowAllAccess(boolean enabled) {
			
			this.builder = this.builder.allowAllAccess(enabled);
			
			return this;
		}
		
		public PolyglotContextBuilder allowCreateProcess(boolean enabled) {
			
			this.builder = this.builder.allowCreateProcess(enabled);
			
			return this;
		}
		
		public PolyglotContextBuilder allowCreateThread(boolean enabled) {
			
			this.builder = this.builder.allowCreateThread(enabled);
			
			return this;
		}
		
		public PolyglotContextBuilder allowEnvironmentAccess(EnvironmentAccess accessPolicy) {
			
			this.builder = this.builder.allowEnvironmentAccess(accessPolicy);
			
			return this;
		}
		
		public PolyglotContextBuilder allowEnvironmentAccess(PolyglotEnvironmentAccess accessPolicy) {
			
			return this.allowEnvironmentAccess(accessPolicy.getAccess());
		}
		
		public PolyglotContextBuilder allowExperimentalOptions(boolean enabled) {
			
			this.builder = this.builder.allowExperimentalOptions(enabled);
			
			return this;
		}
		
		public PolyglotContextBuilder allowHostAccess(HostAccess config) {
			
			this.builder = this.builder.allowHostAccess(config);
			
			return this;
		}
		
		public PolyglotContextBuilder allowHostAccess(PolyglotHostAccess config) {
			
			return this.allowHostAccess(config.getAccess());
		}
		
		public PolyglotContextBuilder allowHostClassLoading(boolean enabled) {
			
			this.builder = this.builder.allowHostClassLoading(enabled);
			
			return this;
		}
		
		public PolyglotContextBuilder allowHostClassLookup(Predicate<String> classFilter) {
			
			this.builder = this.builder.allowHostClassLookup(classFilter);
			
			return this;
		}
		
		public PolyglotContextBuilder allowIO(boolean enabled) {
			
			this.builder = this.builder.allowIO(enabled);
			
			return this;
		}
		
		public PolyglotContextBuilder allowNativeAccess(boolean enabled) {
			
			this.builder = this.builder.allowNativeAccess(enabled);
			
			return this;
		}
		
		public PolyglotContextBuilder allowPolyglotAccess(org.graalvm.polyglot.PolyglotAccess accessPolicy) {
			
			this.builder = this.builder.allowPolyglotAccess(accessPolicy);
			
			return this;
		}
		
		public PolyglotContextBuilder allowPolyglotAccess(PolyglotAccess accessPolicy) {
			
			return this.allowPolyglotAccess(accessPolicy.getAccess());
		}
		
		public PolyglotContextBuilder allowValueSharing(boolean enabled) {
			
			this.builder = this.builder.allowValueSharing(enabled);
			
			return this;
		}
		
		public PolyglotContextBuilder arguments(String language, String[] args) {
			
			this.builder = this.builder.arguments(language, args);
			
			return this;
		}
		
		public PolyglotContext build() {
			
			return new PolyglotContext(this.builder.build());
		}
		
		public PolyglotContextBuilder currentWorkingDirectory(Path workingDirectory) {
			
			this.builder = this.builder.currentWorkingDirectory(workingDirectory);
			
			return this;
		}
		
		public PolyglotContextBuilder engine(org.graalvm.polyglot.Engine engine) {
			
			this.builder = this.builder.engine(engine);
			
			return this;
		}
		
		public PolyglotContextBuilder engine(PolyglotEngine engine) {
			
			return this.engine(engine.getEngine());
		}
		
		public PolyglotContextBuilder environment(Map<String,String> env) {
			
			this.builder = this.builder.environment(env);
			
			return this;
		}
		
		public PolyglotContextBuilder environment(String name, String value) {
			
			this.builder = this.builder.environment(name, value);
			
			return this;
		}
		
		public PolyglotContextBuilder err(OutputStream err) {
			
			this.builder = this.builder.err(err);
			
			return this;
		}
		
		public PolyglotContextBuilder fileSystem(FileSystem fileSystem) {
			
			this.builder = this.builder.fileSystem(fileSystem);
			
			return this;
		}
		
		public PolyglotContextBuilder fileSystem(PolyglotFileSystem fileSystem) {
			
			return this.fileSystem(fileSystem.getSystem());
		}
		
		public PolyglotContextBuilder hostClassLoader(ClassLoader classLoader) {
			
			this.builder = this.builder.hostClassLoader(classLoader);
			
			return this;
		}
		
		public PolyglotContextBuilder in(InputStream in) {
			
			this.builder = this.builder.in(in);
			
			return this;
		}
		
		public PolyglotContextBuilder logHandler(Handler logHandler) {
			
			this.builder = this.builder.logHandler(logHandler);
			
			return this;
		}
		
		public PolyglotContextBuilder logHandler(OutputStream logOut) {
			
			this.builder = this.builder.logHandler(logOut);
			
			return this;
		}
		
		public PolyglotContextBuilder option(String key, String value) {
			
			this.builder = this.builder.option(key, value);
			
			return this;
		}
		
		public PolyglotContextBuilder options(Map<String,String> options){
			
			this.builder = this.builder.options(options);
			
			return this;
		}
		
		public PolyglotContextBuilder out(OutputStream out) {
			
			this.builder = this.builder.out(out);
			
			return this;
		}
		
		public PolyglotContextBuilder processHandler(ProcessHandler handler) {
			
			this.builder = this.builder.processHandler(handler);
			
			return this;
		}
		
		public PolyglotContextBuilder processHandler(PolyglotProcessHandler handler) {
			
			return this.processHandler(handler.getHandler());
		}
		
		public PolyglotContextBuilder resourceLimits(ResourceLimits limits) {
			
			this.builder = this.builder.resourceLimits(limits);
			
			return this;
		}
		
		public PolyglotContextBuilder resourceLimits(PolyglotResourceLimits limits) {
			
			return this.resourceLimits(limits.getLimits());
		}
		
		public PolyglotContextBuilder serverTransport(MessageTransport serverTransport) {
			
			this.builder = this.builder.serverTransport(serverTransport);
			
			return this;
		}
		
		public PolyglotContextBuilder serverTransport(PolyglotMessageTransport serverTransport) {
			
			return this.serverTransport(serverTransport.getTransport());
		}
		
		public PolyglotContextBuilder timeZone(ZoneId zone) {
			
			this.builder = this.builder.timeZone(zone);
			
			return this;
		}
	}
	
	public static final class PolyglotContextCloseEvent extends Event {
		
		public PolyglotContextCloseEvent(EventHandler... handlers) {
			
			super(handlers);
		}
		
	}
	
	
	
}
