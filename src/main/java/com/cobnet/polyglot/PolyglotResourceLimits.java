package com.cobnet.polyglot;

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
		
		private final ResourceLimits.Builder builder;
		
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

			this.builder.onLimit(t -> onLimit.accept(new PolyglotResourceLimitEvent(t)));

			return this;
		}
		
		public PolyglotResourceLimitsBuilder statementLimit(long limit, Predicate<PolyglotSource> sourceFilter) {

			this.builder.statementLimit(limit, t -> sourceFilter.test(new PolyglotSource(t)));

			return this;
		}
	}
}
