package com.cobnet.interfaces.spring.controller.annotations;

import java.lang.annotation.*;

@Target({ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface EntityPageHint {

    int number() default 0;

    int size() default 20;

    //@Language("spel")
    String value() default "";
}
