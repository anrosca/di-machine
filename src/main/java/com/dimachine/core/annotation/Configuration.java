package com.dimachine.core.annotation;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Configuration {
    String value() default "";

    boolean proxyBeanMethods() default true;
}
