package com.cobnet.interfaces.connection.annotation;

import com.cobnet.connection.NettyServer;
import com.cobnet.spring.boot.controller.handler.InboundOperation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EventHandler {

    public InboundOperation[] value() default { InboundOperation.UNKNOWN };

    public Class<? extends NettyServer<?>>[] allowed() default {};
}
