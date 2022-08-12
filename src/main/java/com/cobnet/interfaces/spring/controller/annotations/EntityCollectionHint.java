package com.cobnet.interfaces.spring.controller.annotations;

import java.lang.annotation.*;

@Target({ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface EntityCollectionHint {

    EntityOrderHint[] orderBy() default {};

    EntityPageHint pagination() default @EntityPageHint;
    boolean distinct() default false;

    long total() default -1;
}
