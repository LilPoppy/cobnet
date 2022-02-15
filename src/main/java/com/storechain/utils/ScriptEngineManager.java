package com.storechain.utils;

import com.storechain.EntryPoint;
import com.storechain.interfaces.EventHandler;
import com.storechain.polyglot.PolyglotContext;
import com.storechain.polyglot.PolyglotEngine;
import com.storechain.polyglot.PolyglotLanguage;
import com.storechain.polyglot.PolyglotSource;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class ScriptEngineManager {
	
	private static final ScriptEngineManager INSTANCE = new ScriptEngineManager();
	
	private final List<PolyglotContext> contexts = new ArrayList<PolyglotContext>();
	
	private ScriptEngineManager() {}
	
	public static Collection<PolyglotLanguage> getPolyglotLanguages() {
		
		try (PolyglotEngine engine = PolyglotEngine.create()) {
		
			return engine.getLanguages().values();
		}
	}
	
	public static List<String> getAvailableLanguages() {
		
		return getPolyglotLanguages().stream().map(lang -> lang.getName()).toList();
	}
	
	public static List<PolyglotContext> getContexts() {
		
		return Collections.unmodifiableList(INSTANCE.contexts);
	}
	
	public static PolyglotContext newContext(String... permittedLanguages) {
		
		PolyglotContext context = PolyglotContext.create(permittedLanguages);
		
		context.closeEvent.addHandler(new EventHandler() {

			@Override
			public void notify(Event event, Object... args) {

				INSTANCE.contexts.remove(context);
			}});
		
		INSTANCE.contexts.add(context);
		
		return context;
	}
	
	public static PolyglotContext firstOrNewContext() {
		
		if(INSTANCE.contexts.size() > 0) {

			return INSTANCE.contexts.stream().findFirst().get();
		}
		
		return newContext();
	}
	
	public static void removeContext(PolyglotContext context) {
		
		context.close();
	}
	
	public static void removeContext(PolyglotContext context, boolean cancelIfExecuting) {
		
		context.close(cancelIfExecuting);
	}
	
}
