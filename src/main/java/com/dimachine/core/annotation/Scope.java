package com.dimachine.core.annotation;

import com.dimachine.core.BeanScope;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Scope {
    BeanScope value() default BeanScope.SINGLETON;
}
