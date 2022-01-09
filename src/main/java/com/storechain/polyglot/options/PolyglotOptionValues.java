package com.storechain.polyglot.options;

import org.graalvm.options.OptionValues;
import org.graalvm.options.OptionKey;

public class PolyglotOptionValues {

	private final OptionValues values;
	
	public PolyglotOptionValues(OptionValues values) {
		
		this.values = values;
	}

	public OptionValues getValues() {
		
		return values;
	}
	
	public <T> T get(OptionKey<T> optionKey) {
		
		return this.values.get(optionKey);
	}
	
	public <T> T get(PolyglotOptionKey<T> optionKey) {
		
		return this.get(optionKey.getKey());
	}
	
	public PolyglotOptionDescriptors getDescriptors() {
		
		return new PolyglotOptionDescriptors(this.values.getDescriptors());
	}
	
	public boolean hasBeenSet(OptionKey<?> optionKey) {
		
		return this.values.hasBeenSet(optionKey); 
	}
	
	public boolean hasBeenSet(PolyglotOptionKey<?> optionKey) {
		
		return this.hasBeenSet(optionKey.getKey());
	}
	
	public boolean hasSetOptions() {
		
		return this.values.hasSetOptions();
	}
}
