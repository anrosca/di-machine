package com.dimachine.core.annotation;

import com.dimachine.core.Order;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Ordered {
    Order value() default Order.DEFAULT_PRECEDENCE;
}
