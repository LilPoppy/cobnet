package com.storechain.interfaces.connection.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.storechain.connection.InboundOperation;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ConnectionHandler {
	
	public InboundOperation operation() default InboundOperation.UNKNOWN;
	
	public InboundOperation[] operations() default {};
}
