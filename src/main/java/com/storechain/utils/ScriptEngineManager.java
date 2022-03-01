package com.storechain.utils;

import com.storechain.common.Event;
import com.storechain.interfaces.EventHandler;
import com.storechain.polyglot.PolyglotContext;
import com.storechain.polyglot.PolyglotEngine;
import com.storechain.polyglot.PolyglotLanguage;
import com.storechain.polyglot.PolyglotSource;
import com.storechain.polyglot.PolyglotValue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

public class ScriptEngineManager {
	
	private static final ScriptEngineManager INSTANCE = new ScriptEngineManager();
	
	private final Map<String, PolyglotContext> contexts = new ConcurrentHashMap<String, PolyglotContext>();
	
	private ScriptEngineManager() {}
	
	public static Collection<PolyglotLanguage> getPolyglotLanguages() {
		
		try (PolyglotEngine engine = PolyglotEngine.create()) {
		
			return engine.getLanguages().values();
		}
	}
	
	public static List<String> getAvailableLanguages() {
		
		return getPolyglotLanguages().stream().map(lang -> lang.getName()).toList();
	}
	
	public static Map<String, PolyglotContext> getContexts() {
		
		return Collections.unmodifiableMap(INSTANCE.contexts);
	}
	
	public static PolyglotContext newContext(String name, String... permittedLanguages) {
		
	    PolyglotContext old = INSTANCE.contexts.get(name);
	    
	    if(old != null) {
	    	
	    	old.close(true);
	    }
		
		PolyglotContext context = PolyglotContext.create(permittedLanguages);
		
		context.closeEvent.addHandler(new EventHandler() {

			@Override
			public void notify(Event event, Object... args) {

				INSTANCE.contexts.remove(name);
			}});
		
		INSTANCE.contexts.put(name, context);
		
		return context;
	}
	
	public static PolyglotContext getOrNewContext(String name) {
		
		if(INSTANCE.contexts.size() > 0) {

			return INSTANCE.contexts.getOrDefault(name, newContext(name));
		}
		
		return newContext(name);
	}
	
	public static PolyglotContext firstOrNewContext() {
		
		if(INSTANCE.contexts.size() > 0) {
			
			return INSTANCE.contexts.values().stream().findFirst().get();
		}
		
		return newContext("undefined");
	}
	
	public static PolyglotValue eval(PolyglotContext context, PolyglotSource source) {
		
		return context.eval(source);
	}
	
	public static PolyglotValue evalFromResource(PolyglotContext context, String language, String path) {
		
		return eval(context, PolyglotSource.readFromResource(language, path));
	}
	
	public static PolyglotValue evalFromResource(String name, String language, String path) {
		
		return evalFromResource(getOrNewContext(name), language, path);
	}
	
	public static PolyglotValue evalFromResource(String language, String path) {
		
		return evalFromResource(firstOrNewContext(), language, path);
	}
	
	public static PolyglotValue evalFromFile(PolyglotContext context, String language, File file) throws FileNotFoundException, IOException {
		
		return eval(context, PolyglotSource.readFromFile(language, file));
	}
	
	public static PolyglotValue evalFromFile(PolyglotContext context, String language, String file) throws FileNotFoundException, IOException {
		
		return eval(context, PolyglotSource.readFromFIle(language, file));
	}
	
	public static PolyglotValue evalFromFile(String name, String language, File file) throws FileNotFoundException, IOException {
		
		return evalFromFile(getOrNewContext(name), language, file);
	}
	
	public static PolyglotValue evalFromFile(String language, File file) throws FileNotFoundException, IOException {
		
		return evalFromFile(firstOrNewContext(), language, file);
	}
	
	public static PolyglotValue evalFromFile(String name, String language, String path) throws FileNotFoundException, IOException {
		
		return evalFromFile(getOrNewContext(name), language, path);
	}
	
	public static PolyglotValue evalFromFile(String language, String path) throws FileNotFoundException, IOException {
		
		return evalFromFile(firstOrNewContext(), language, path);
	}
	
	public static PolyglotValue eval(String name, PolyglotSource source) {
		
		return getOrNewContext(name).eval(source);
	}
	
	public static PolyglotValue eval(PolyglotSource source) {
		
		return firstOrNewContext().eval(source);
	}
	
	public static PolyglotValue execute(PolyglotContext context, String language, String member, Object... args) {
		
		return context.getBindings(language).getMember(member).execute(args);
	}
	
	public static PolyglotValue execute(String name, String language, String member, Object... args) {
		
		return execute(getOrNewContext(name), language, member, args);
	}
	
	public static PolyglotValue execute(String language, String member, Object... args) {
		
		return execute(firstOrNewContext(), language, member, args);
	}
	
	public static void removeContext(PolyglotContext context) {
		
		context.close();
	}
	
	public static void removeContext(PolyglotContext context, boolean cancelIfExecuting) {
		
		context.close(cancelIfExecuting);
	}
	
}
