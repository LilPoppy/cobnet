package com.storechain.polyglot.options;

import org.graalvm.options.OptionDescriptor;
import org.graalvm.options.OptionDescriptor.Builder;
import org.graalvm.options.OptionStability;
import org.graalvm.options.OptionKey;
import org.graalvm.options.OptionCategory;

public class PolyglotOptionDescriptor {
	
	private final OptionDescriptor descriptor;
	
	public PolyglotOptionDescriptor(OptionDescriptor descriptor) {
		
		this.descriptor = descriptor;
	}

	public OptionDescriptor getDescriptor() {
		
		return descriptor;
	}
	
	@Override
	public boolean equals(Object obj) {
		
		return this.descriptor.equals(obj);
	}
	
	public org.graalvm.options.OptionCategory getCategory() {
		
		return this.descriptor.getCategory();
	}
	
	public String getDeprecationMessage() {
		
		return this.descriptor.getDeprecationMessage();
	}
	
	public String getHelp() {
		
		return this.descriptor.getHelp();
	}
	
	public PolyglotOptionKey<?> getKey() {
		
		return new PolyglotOptionKey<>(this.descriptor.getKey());
	}
	
	public String getName() {
		
		return this.descriptor.getName();
	}
	
	public OptionStability getStability() {
		
		return this.descriptor.getStability();
	}
	
	@Override
	public int hashCode() {
		
		return this.descriptor.hashCode();
	}
	
	public boolean isDeprecated() {
		
		return this.descriptor.isDeprecated();
	}
	
	public boolean isOptionMap() {
		
		return this.descriptor.isOptionMap();
	}
	
	public static <T> PolyglotOptionDescriptorBuilder newBuilder(OptionKey<T> key, String name) {
		
		return new PolyglotOptionDescriptorBuilder(OptionDescriptor.newBuilder(key, name));
	}
	
	public static <T> PolyglotOptionDescriptorBuilder newBuilder(PolyglotOptionKey<T> key, String name) {
		
		return PolyglotOptionDescriptor.newBuilder(key.getKey(), name);
	}
	
	@Override
	public String toString() {
		
		return this.descriptor.toString();
	}
	
	public final static class PolyglotOptionDescriptorBuilder {
		
		private Builder builder;
		
		public PolyglotOptionDescriptorBuilder(Builder builder) {
			
			this.builder = builder;
		}
		
		public Builder getBuilder() {
			
			return this.builder;
		}
		
		public PolyglotOptionDescriptor build() {
			
			return new PolyglotOptionDescriptor(this.builder.build());
		}
		
		public PolyglotOptionDescriptorBuilder category(OptionCategory category) {
			
			this.builder = this.builder.category(category);
			
			return this;
		}
		
		public PolyglotOptionDescriptorBuilder deprecated(boolean deprecated) {
			
			this.builder = this.builder.deprecated(deprecated);
			
			return this;
		}
		
		public PolyglotOptionDescriptorBuilder deprecationMessage(String deprecationMessage) {
			
			this.builder = this.builder.deprecationMessage(deprecationMessage);
			
			return this;
		}
		
		public PolyglotOptionDescriptorBuilder help(String help) {
			
			this.builder = this.builder.help(help);
			
			return this;
		}
		
		public PolyglotOptionDescriptorBuilder stability(OptionStability stability) {
			
			this.builder = this.builder.stability(stability);
			
			return this;
		}
	}

}
