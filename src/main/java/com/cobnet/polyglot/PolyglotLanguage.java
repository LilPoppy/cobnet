package com.cobnet.polyglot;

import com.cobnet.polyglot.options.PolyglotOptionDescriptors;
import org.graalvm.polyglot.Language;

import java.util.Set;

public final class PolyglotLanguage {

	private final Language language;
	
	public PolyglotLanguage(Language language) {
		
		this.language = language;
	}

	public Language getLanguage() {
		
		return language;
	}
	
	public String getDefaultMimeType() {
		
		return this.language.getDefaultMimeType();
	}
	
	public String getId() {
		
		return this.language.getId();
	}
	
	public String getImplementationName() {
		
		return this.language.getImplementationName();
	}
	
	public Set<String> getMimeTypes() {
		
		return this.language.getMimeTypes();
	}
	
	public String getName() {
		
		return this.language.getName();
	}
	
	public PolyglotOptionDescriptors getOptions() {
		
		return new PolyglotOptionDescriptors(this.language.getOptions());
	}
	
	public String getVersion() {
		
		return this.language.getVersion();
	}
	
	public boolean isInteractive() {
		
		return this.language.isInteractive();
	}
}
