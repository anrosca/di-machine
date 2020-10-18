package com.dimachine.core.annotation;

import com.dimachine.core.BeanScope;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Scope {
    BeanScope value() default BeanScope.SINGLETON;
}
