package com.dimachine.core.annotation;

import java.lang.annotation.*;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ComponentScans {
    ComponentScan[] value() default {};
}
