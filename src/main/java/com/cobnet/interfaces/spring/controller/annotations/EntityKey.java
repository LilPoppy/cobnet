package com.cobnet.interfaces.spring.controller.annotations;

import com.cobnet.spring.boot.controller.support.enums.ComparisonOperator;
import com.cobnet.spring.boot.controller.support.enums.ConjunctiveOperator;

import java.lang.annotation.*;

@Target({ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented

public @interface EntityKey {

    //@Language("spel")
    String[] keys();

    boolean reversed() default false;

    ComparisonOperator operator() default ComparisonOperator.IS;

    ConjunctiveOperator conjunctive() default ConjunctiveOperator.AND;
}
