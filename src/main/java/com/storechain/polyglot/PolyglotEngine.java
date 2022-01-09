package com.storechain.polyglot;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;
import java.util.logging.Handler;
import java.util.stream.Collectors;

import org.graalvm.polyglot.Engine;
import org.graalvm.polyglot.io.MessageTransport;

import com.storechain.polyglot.io.PolyglotMessageTransport;
import com.storechain.polyglot.options.PolyglotOptionDescriptors;

public final class PolyglotEngine implements AutoCloseable {

	private final Engine engine;
	
	public PolyglotEngine(Engine engine) {
		
		this.engine = engine;
	}
	
	public PolyglotEngine() {
		
		this.engine = Engine.create();
	}

	public Engine getEngine() {
		return engine;
	}
	
	@Override
	public void close() {
		
		this.engine.close();
	}
	
	public void close(boolean cancelIfExecuting) {
		
		this.engine.close(cancelIfExecuting);
	}
	
	public static PolyglotEngine create() {
		
		return new PolyglotEngine(Engine.create());
	}
	
	public static Path findHome() {
		
		return Engine.findHome();
	}
	
	public Set<PolyglotSource> getCachedSources() {
		
		return this.engine.getCachedSources().stream().map(source -> new PolyglotSource(source)).collect(Collectors.toSet());
	}
	
	public String getImplementationName() {
		
		return this.engine.getImplementationName();
	}
	
	public Map<String,PolyglotInstrument> getInstruments() {
		
		return this.engine.getInstruments().entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> new PolyglotInstrument(entry.getValue())));
	}
	
	public Map<String, PolyglotLanguage> getLanguages() {
		
		return this.engine.getLanguages().entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> new PolyglotLanguage(entry.getValue())));
	}
	
	public PolyglotOptionDescriptors getOptions() {
		
		return new PolyglotOptionDescriptors(this.engine.getOptions());
	}
	
	public String getVersion() {
		
		return this.engine.getVersion();
	}
	
	public static PolyglotEngineBuilder newBuilder() {
		
		return new PolyglotEngineBuilder(Engine.newBuilder());
	}
	
	public static final class PolyglotEngineBuilder {
		
		private Engine.Builder builder;
		
		public PolyglotEngineBuilder(Engine.Builder builder) {
			
			this.builder = builder;
		}
		
		public Engine.Builder getBuilder() {
			
			return this.builder;
		}
		
		public PolyglotEngineBuilder allowExperimentalOptions(boolean enabled) {
			
			this.builder = this.builder.allowExperimentalOptions(enabled);
			
			return this;
		}
		
		public PolyglotEngine build() {
			
			return new PolyglotEngine(this.builder.build());
		}
		
		public PolyglotEngineBuilder err(OutputStream err) {
			
			this.builder = this.builder.err(err);
			
			return this;
		}
		
		public PolyglotEngineBuilder in(InputStream in) {
			
			this.builder = this.builder.in(in);
			
			return this;
		}
		
		public PolyglotEngineBuilder logHandler(Handler logHandler) {
			
			this.builder = this.builder.logHandler(logHandler);
			
			return this;
		}
		
		public PolyglotEngineBuilder logHandler(OutputStream logOut) {
			
			this.builder = this.builder.logHandler(logOut);
			
			return this;
		}
		
		public PolyglotEngineBuilder option(String key, String value) {
			
			this.builder = this.builder.option(key, value);
			
			return this;
		}
		
		public PolyglotEngineBuilder options(Map<String,String> options) {
			
			this.builder = this.builder.options(options);
			
			return this;
		}
		
		public PolyglotEngineBuilder out(OutputStream out) {
			
			this.builder = this.builder.out(out);
			
			return this;
		}
		
		public PolyglotEngineBuilder serverTransport(MessageTransport serverTransport) {
			
			this.builder = this.builder.serverTransport(serverTransport);
			
			return this;
		}
		
		public PolyglotEngineBuilder serverTransport(PolyglotMessageTransport serverTransport) {
			
			return this.serverTransport(serverTransport.getTransport());
		}
		
		public PolyglotEngineBuilder useSystemProperties(boolean enabled) {
			
			this.builder = this.builder.useSystemProperties(enabled);
			
			return this;
		}
		
		
	}

}
