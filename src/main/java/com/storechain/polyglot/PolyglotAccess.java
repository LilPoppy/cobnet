package com.storechain.polyglot;

public final class PolyglotAccess {
	
	private final org.graalvm.polyglot.PolyglotAccess access;
	
	public final static PolyglotAccess ALL = new PolyglotAccess(org.graalvm.polyglot.PolyglotAccess.ALL);
	
	public final static PolyglotAccess NONE = new PolyglotAccess(org.graalvm.polyglot.PolyglotAccess.NONE);

	public PolyglotAccess(org.graalvm.polyglot.PolyglotAccess access) {
		
		this.access = access;
	}

	public org.graalvm.polyglot.PolyglotAccess getAccess() {
		
		return access;
	}
	
	public static PolyglotAccessBuilder newBuilder() {
		
		return new PolyglotAccessBuilder(org.graalvm.polyglot.PolyglotAccess.newBuilder());
	}
	
	
	public static final class PolyglotAccessBuilder {
		
		private org.graalvm.polyglot.PolyglotAccess.Builder builder;
		
		public PolyglotAccessBuilder(org.graalvm.polyglot.PolyglotAccess.Builder builder) {
			
			this.builder = builder;
		}
		

		public org.graalvm.polyglot.PolyglotAccess.Builder getBuilder() {
			
			return builder;
		}
		
		public PolyglotAccessBuilder allowBindingsAccess(String language) {
			
			this.builder = this.builder.allowBindingsAccess(language);
			
			return this;
		}
		
		public PolyglotAccessBuilder allowEval(String from, String to) {
			
			this.builder = this.builder.allowEval(from, to);
			
			return this;
		}
		
		public PolyglotAccessBuilder allowEvalBetween(String... languages) {
			
			this.builder = this.builder.allowEvalBetween(languages);
			
			return this;
		}
		
		public PolyglotAccess build() {
			
			return new PolyglotAccess(this.builder.build());
		}
		
		public PolyglotAccessBuilder denyBindingsAccess(String language) {
			
			this.builder = this.builder.denyBindingsAccess(language);
			
			return this;
		}
		
		public PolyglotAccessBuilder denyEval(String from, String to) {
			
			this.builder = this.builder.denyEval(from, to);
			
			return this;
		}
		
		public PolyglotAccessBuilder denyEvalBetween(String... languages) {
			
			this.builder = this.builder.denyEvalBetween(languages);
			
			return this;
		}
	}
}
