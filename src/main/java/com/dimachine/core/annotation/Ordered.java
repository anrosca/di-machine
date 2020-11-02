package com.dimachine.core.annotation;

import com.dimachine.core.Order;

import java.lang.annotation.*;

@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Ordered {
    Order value() default Order.DEFAULT_PRECEDENCE;
}
