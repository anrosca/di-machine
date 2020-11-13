package com.dimachine.core.annotation;

import java.lang.annotation.*;

@Inherited
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(PropertySources.class)
public @interface PropertySource {
    String value() default "";

    boolean ignoreIfNotFound() default false;
}
