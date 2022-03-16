package com.cobnet.polyglot;

import java.lang.annotation.Annotation;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.util.function.Function;
import java.util.function.Predicate;

import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.HostAccess.Builder;

public final class PolyglotHostAccess {
	
	private final HostAccess access;
	
	public static final PolyglotHostAccess ALL = new PolyglotHostAccess(HostAccess.ALL);
	
	public static final PolyglotHostAccess EXPLICIT = new PolyglotHostAccess(HostAccess.EXPLICIT);
	
	public static final PolyglotHostAccess NONE = new PolyglotHostAccess(HostAccess.NONE);
	
	public static final PolyglotHostAccess SCOPED = new PolyglotHostAccess(HostAccess.SCOPED);
	
	public PolyglotHostAccess(HostAccess access) {
		
		this.access = access;
	}

	public HostAccess getAccess() {
		return access;
	}
	
	@Override
	public boolean equals(Object obj) {
		
		return this.access.equals(obj);
	}
	
	@Override
	public int hashCode() {
		
		return this.access.hashCode();
	}
	
	public static PolyglotHostAccessBuilder newBuilder() {
		
		return new PolyglotHostAccessBuilder(HostAccess.newBuilder());
	}
	
	public static PolyglotHostAccessBuilder newBuilder(HostAccess conf) {
		
		return new PolyglotHostAccessBuilder(HostAccess.newBuilder(conf));
	}
	
	public static PolyglotHostAccessBuilder newBuilder(PolyglotHostAccess conf) {
		
		return PolyglotHostAccess.newBuilder(conf.access);
	}
	
	@Override
	public String toString() {
		
		return this.access.toString();
	}
	
	public static final class PolyglotHostAccessBuilder {
		
		private final Builder builder;
		
		public PolyglotHostAccessBuilder(Builder builder) {
			
			this.builder = builder;
		}
		
		public Builder getBuilder() {
			
			return builder;
		}
		
		public PolyglotHostAccessBuilder allowAccess(Executable element) {

			this.builder.allowAccess(element);

			return this;
		}
		
		public PolyglotHostAccessBuilder allowAccess(Field element) {

			this.builder.allowAccess(element);

			return this;
		}
		
		public PolyglotHostAccessBuilder allowAccessAnnotatedBy(Class<? extends Annotation> annotation) {

			this.builder.allowAccessAnnotatedBy(annotation);

			return this;
		}
		
		public PolyglotHostAccessBuilder allowAllClassImplementations(boolean allow) {

			this.builder.allowAllClassImplementations(allow);

			return this;
		}
		
		public PolyglotHostAccessBuilder allowAllImplementations(boolean allow) {

			this.builder.allowAllImplementations(allow);

			return this;
		}
		
		public PolyglotHostAccessBuilder allowArrayAccess(boolean arrayAccess) {

			this.builder.allowArrayAccess(arrayAccess);

			return this;
		}
		
		public PolyglotHostAccessBuilder allowBufferAccess(boolean bufferAccess){

			this.builder.allowBufferAccess(bufferAccess);

			return this;
		}
		
		public PolyglotHostAccessBuilder allowImplementations(Class<?> type) {

			this.builder.allowImplementations(type);

			return this;
		}
		
		public PolyglotHostAccessBuilder allowImplementationsAnnotatedBy(Class<? extends Annotation> annotation) {

			this.builder.allowImplementationsAnnotatedBy(annotation);

			return this;
		}
		
		public PolyglotHostAccessBuilder allowIterableAccess(boolean iterableAccess) {

			this.builder.allowIterableAccess(iterableAccess);

			return this;
		}
		
		public PolyglotHostAccessBuilder allowIteratorAccess(boolean iteratorAccess){

			this.builder.allowIteratorAccess(iteratorAccess);

			return this;
		}
		
		public PolyglotHostAccessBuilder allowListAccess(boolean listAccess) {

			this.builder.allowListAccess(listAccess);

			return this;
		}
		
		public PolyglotHostAccessBuilder allowMapAccess(boolean mapAccess) {

			this.builder.allowMapAccess(mapAccess);

			return this;
		}
		
		public PolyglotHostAccessBuilder allowPublicAccess(boolean allow) {

			this.builder.allowPublicAccess(allow);

			return this;
		}
		
		public PolyglotHostAccess build() {
			
			return new PolyglotHostAccess(this.builder.build());
		}
		
		public PolyglotHostAccessBuilder denyAccess(Class<?> clazz) {
			
			this.builder.denyAccess(clazz);
			
			return this;
		}
		
		public PolyglotHostAccessBuilder denyAccess(Class<?> clazz, boolean includeSubclasses) {

			this.builder.denyAccess(clazz, includeSubclasses);

			return this;
		}
		
		public PolyglotHostAccessBuilder disableMethodScoping(Executable e) {

			this.builder.disableMethodScoping(e);

			return this;
		}
		
		public PolyglotHostAccessBuilder disableMethodScopingAnnotatedBy(Class<? extends Annotation> annotation) {

			this.builder.disableMethodScopingAnnotatedBy(annotation);

			return this;
		}
		
		public PolyglotHostAccessBuilder methodScoping(boolean scopingDefault) {

			this.builder.methodScoping(scopingDefault);

			return this;
		}
		
		public <S, T> PolyglotHostAccessBuilder targetTypeMapping(Class<S> sourceType, Class<T> targetType, Predicate<S> accepts, Function<S,T> converter) {
			
			this.builder.targetTypeMapping(sourceType, targetType, accepts, converter);
			
			return this;
		}
		
		public <S, T> PolyglotHostAccessBuilder targetTypeMapping(Class<S> sourceType, Class<T> targetType, Predicate<S> accepts, Function<S,T> converter, HostAccess.TargetMappingPrecedence precedence) {

			this.builder.targetTypeMapping(sourceType, targetType, accepts, converter, precedence);

			return this;
		}

	}

}
