package com.cobnet.interfaces.spring.controller.annotations;


import com.cobnet.spring.boot.controller.support.enums.OrderDirection;

import java.lang.annotation.*;

@Target({ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface EntityOrderHint {

    String attribute();

    OrderDirection orderByDirection() default OrderDirection.ASCENDING;
}
