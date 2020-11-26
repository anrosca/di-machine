package com.dimachine.core.annotation;

import java.lang.annotation.*;

@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Component
public @interface Configuration {
    String value() default "";

    boolean proxyBeanMethods() default true;
}
