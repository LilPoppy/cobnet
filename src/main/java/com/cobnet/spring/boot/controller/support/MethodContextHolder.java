package com.cobnet.spring.boot.controller.support;

public class MethodContextHolder {

    static final ThreadLocal<MethodContext> context = new ThreadLocal<>();

    public static MethodContext getContext() {

        return MethodContextHolder.context.get();
    }
}
