package com.cobnet.interfaces.spring.controller.annotations;

import com.cobnet.spring.boot.controller.support.enums.ConjunctiveOperator;

import java.lang.annotation.*;

@Target({ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface EntityAttributeCondition {

    EntityAttribute[] attributes();

    ConjunctiveOperator operator() default ConjunctiveOperator.AND;

}
