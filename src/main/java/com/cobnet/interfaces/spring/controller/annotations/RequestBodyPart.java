package com.cobnet.interfaces.spring.controller.annotations;

import java.lang.annotation.*;

@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.ANNOTATION_TYPE, ElementType.PARAMETER})
public @interface RequestBodyPart {

    String name() default "";

    boolean required() default true;

    boolean isolated() default true;

    String[] referenced() default {};
}
