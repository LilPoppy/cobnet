package com.cobnet.interfaces.spring.controller.annotations;

import org.springframework.core.annotation.AliasFor;
import org.springframework.data.jpa.repository.Query;

import javax.persistence.FlushModeType;
import javax.persistence.QueryHint;
import java.lang.annotation.*;

@Target({ElementType.PARAMETER, ElementType.TYPE_USE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@RequestBodyPart
public @interface EntityVariable {

    @AliasFor(annotation = RequestBodyPart.class)
    String name() default "";

    @AliasFor(annotation = RequestBodyPart.class)
    boolean required() default true;

    @AliasFor(annotation = RequestBodyPart.class)
    boolean isolated() default true;

    FlushModeType flush() default FlushModeType.COMMIT;

    boolean creatable() default false;

    boolean cacheable() default true;

    EntityAttributeCondition[] groups();

    EntityCollectionHint collection() default @EntityCollectionHint;

    QueryHint[] hints() default {};
}
