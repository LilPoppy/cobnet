package com.storechain.polyglot;

import java.util.function.Consumer;
import java.util.function.Predicate;

import org.graalvm.polyglot.ResourceLimits;
import org.graalvm.polyglot.ResourceLimitEvent;
import org.graalvm.polyglot.Source;

public class PolyglotResourceLimits {
	
	private final ResourceLimits limits;
	
	public PolyglotResourceLimits(ResourceLimits limits) {
		
		this.limits = limits;
	}

	public ResourceLimits getLimits() {
		
		return limits;
	}
	
	public static PolyglotResourceLimitsBuilder newBuilder() {
		
		return new PolyglotResourceLimitsBuilder(ResourceLimits.newBuilder());
	}

	public static final class PolyglotResourceLimitsBuilder {
		
		private ResourceLimits.Builder builder;
		
		public PolyglotResourceLimitsBuilder(ResourceLimits.Builder builder) {
			
			this.builder = builder;
		}

		public ResourceLimits.Builder getBuilder() {
			
			return builder;
		}
		
		public PolyglotResourceLimits build() {
			
			return new PolyglotResourceLimits(this.builder.build());
		}
		
		
		public PolyglotResourceLimitsBuilder onLimit(Consumer<PolyglotResourceLimitEvent> onLimit) {
			
			this.builder = this.builder.onLimit(new Consumer<ResourceLimitEvent>() 
			{

				@Override
				public void accept(ResourceLimitEvent t) {
					
					onLimit.accept(new PolyglotResourceLimitEvent(t));
				}
				
			});
			
			return this;
		}
		
		public PolyglotResourceLimitsBuilder statementLimit(long limit, Predicate<PolyglotSource> sourceFilter) {
			
			this.builder = this.builder.statementLimit(limit, new Predicate<Source>() 
			{

				@Override
				public boolean test(Source t) {

					return sourceFilter.test(new PolyglotSource(t));
				}
				
			});
			
			return this;
		}
	}
}
