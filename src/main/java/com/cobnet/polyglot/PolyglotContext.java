package com.cobnet.polyglot;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.time.Duration;
import java.time.ZoneId;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.util.function.Predicate;
import java.util.logging.Handler;

import com.cobnet.event.polyglot.PolyglotContextCloseEvent;
import com.cobnet.polyglot.io.PolyglotFileSystem;
import com.cobnet.polyglot.io.PolyglotMessageTransport;
import com.cobnet.polyglot.io.PolyglotProcessHandler;
import com.cobnet.spring.boot.core.ProjectBeanHolder;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Context.Builder;
import org.graalvm.polyglot.EnvironmentAccess;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.io.FileSystem;
import org.graalvm.polyglot.io.ProcessHandler;


import org.graalvm.polyglot.ResourceLimits;
import org.graalvm.polyglot.io.MessageTransport;

public final class PolyglotContext implements AutoCloseable {

	private final String name;

	private final Context context;

	public PolyglotContext(String name, String... permittedLanguages) {

		this.name = name;

		if(permittedLanguages.length > 0) {
			
			this.context = Context.create(permittedLanguages);
			
		} else {
			
			this.context = Context.create();
		}
	}
	
	public PolyglotContext(String name, Context context) {

		this.name = name;
		this.context = context;
	}

	public String getName() {

		return this.name;
	}
	
	public Context getContext() {
		return this.context;
	}

    public void close(boolean cancelIfExecuting) {

		PolyglotContextCloseEvent event = new PolyglotContextCloseEvent(this, cancelIfExecuting);

		ProjectBeanHolder.getApplicationEventPublisher().publishEvent(event);
    	
    	this.context.close(cancelIfExecuting);
    }
    
	@Override
	public void close() {

		PolyglotContextCloseEvent event = new PolyglotContextCloseEvent(this, false);

		ProjectBeanHolder.getApplicationEventPublisher().publishEvent(event);
		
		this.context.close();
	}
	
	public static PolyglotContext create(String name, String... permittedLanguages) {
		
		return new PolyglotContext(name, permittedLanguages);
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

		Context context = Context.getCurrent();

		return ProjectBeanHolder.getScriptEngineManager().stream().filter(managed -> managed.getContext().equals(context)).findFirst().orElse(new PolyglotContext("unmanaged", context));
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
		
		private final Builder builder;
		
		public PolyglotContextBuilder(Builder builder) {
			
			this.builder = builder;
		}
		
		public Builder getBuilder() {
			
			return this.builder;
		}
		
		public PolyglotContextBuilder allowAllAccess(boolean enabled) {

			this.builder.allowAllAccess(enabled);

			return this;
		}
		
		public PolyglotContextBuilder allowCreateProcess(boolean enabled) {

			this.builder.allowCreateProcess(enabled);

			return this;
		}
		
		public PolyglotContextBuilder allowCreateThread(boolean enabled) {

			this.builder.allowCreateThread(enabled);

			return this;
		}
		
		public PolyglotContextBuilder allowEnvironmentAccess(EnvironmentAccess accessPolicy) {

			this.builder.allowEnvironmentAccess(accessPolicy);

			return this;
		}
		
		public PolyglotContextBuilder allowEnvironmentAccess(PolyglotEnvironmentAccess accessPolicy) {
			
			return this.allowEnvironmentAccess(accessPolicy.getAccess());
		}
		
		public PolyglotContextBuilder allowExperimentalOptions(boolean enabled) {

			this.builder.allowExperimentalOptions(enabled);

			return this;
		}
		
		public PolyglotContextBuilder allowHostAccess(HostAccess config) {

			this.builder.allowHostAccess(config);

			return this;
		}
		
		public PolyglotContextBuilder allowHostAccess(PolyglotHostAccess config) {
			
			return this.allowHostAccess(config.getAccess());
		}
		
		public PolyglotContextBuilder allowHostClassLoading(boolean enabled) {

			this.builder.allowHostClassLoading(enabled);

			return this;
		}
		
		public PolyglotContextBuilder allowHostClassLookup(Predicate<String> classFilter) {

			this.builder.allowHostClassLookup(classFilter);

			return this;
		}
		
		public PolyglotContextBuilder allowIO(boolean enabled) {

			this.builder.allowIO(enabled);

			return this;
		}
		
		public PolyglotContextBuilder allowNativeAccess(boolean enabled) {

			this.builder.allowNativeAccess(enabled);

			return this;
		}
		
		public PolyglotContextBuilder allowPolyglotAccess(org.graalvm.polyglot.PolyglotAccess accessPolicy) {

			this.builder.allowPolyglotAccess(accessPolicy);

			return this;
		}
		
		public PolyglotContextBuilder allowPolyglotAccess(PolyglotAccess accessPolicy) {
			
			return this.allowPolyglotAccess(accessPolicy.getAccess());
		}
		
		public PolyglotContextBuilder allowValueSharing(boolean enabled) {

			this.builder.allowValueSharing(enabled);

			return this;
		}
		
		public PolyglotContextBuilder arguments(String language, String[] args) {

			this.builder.arguments(language, args);

			return this;
		}
		
		public PolyglotContext build(String name) {
			
			return new PolyglotContext(name, this.builder.build());
		}
		
		public PolyglotContextBuilder currentWorkingDirectory(Path workingDirectory) {

			this.builder.currentWorkingDirectory(workingDirectory);

			return this;
		}
		
		public PolyglotContextBuilder engine(org.graalvm.polyglot.Engine engine) {

			this.builder.engine(engine);

			return this;
		}
		
		public PolyglotContextBuilder engine(PolyglotEngine engine) {
			
			return this.engine(engine.getEngine());
		}
		
		public PolyglotContextBuilder environment(Map<String,String> env) {

			this.builder.environment(env);

			return this;
		}
		
		public PolyglotContextBuilder environment(String name, String value) {

			this.builder.environment(name, value);

			return this;
		}
		
		public PolyglotContextBuilder err(OutputStream err) {

			this.builder.err(err);

			return this;
		}
		
		public PolyglotContextBuilder fileSystem(FileSystem fileSystem) {

			this.builder.fileSystem(fileSystem);

			return this;
		}
		
		public PolyglotContextBuilder fileSystem(PolyglotFileSystem fileSystem) {
			
			return this.fileSystem(fileSystem.getSystem());
		}
		
		public PolyglotContextBuilder hostClassLoader(ClassLoader classLoader) {

			this.builder.hostClassLoader(classLoader);

			return this;
		}
		
		public PolyglotContextBuilder in(InputStream in) {

			this.builder.in(in);

			return this;
		}
		
		public PolyglotContextBuilder logHandler(Handler logHandler) {

			this.builder.logHandler(logHandler);

			return this;
		}
		
		public PolyglotContextBuilder logHandler(OutputStream logOut) {

			this.builder.logHandler(logOut);

			return this;
		}
		
		public PolyglotContextBuilder option(String key, String value) {

			this.builder.option(key, value);

			return this;
		}
		
		public PolyglotContextBuilder options(Map<String,String> options){

			this.builder.options(options);

			return this;
		}
		
		public PolyglotContextBuilder out(OutputStream out) {

			this.builder.out(out);

			return this;
		}
		
		public PolyglotContextBuilder processHandler(ProcessHandler handler) {

			this.builder.processHandler(handler);

			return this;
		}
		
		public PolyglotContextBuilder processHandler(PolyglotProcessHandler handler) {
			
			return this.processHandler(handler.getHandler());
		}
		
		public PolyglotContextBuilder resourceLimits(ResourceLimits limits) {

			this.builder.resourceLimits(limits);

			return this;
		}
		
		public PolyglotContextBuilder resourceLimits(PolyglotResourceLimits limits) {
			
			return this.resourceLimits(limits.getLimits());
		}
		
		public PolyglotContextBuilder serverTransport(MessageTransport serverTransport) {

			this.builder.serverTransport(serverTransport);

			return this;
		}
		
		public PolyglotContextBuilder serverTransport(PolyglotMessageTransport serverTransport) {
			
			return this.serverTransport(serverTransport.getTransport());
		}
		
		public PolyglotContextBuilder timeZone(ZoneId zone) {

			this.builder.timeZone(zone);

			return this;
		}
	}

	
	
	
}
