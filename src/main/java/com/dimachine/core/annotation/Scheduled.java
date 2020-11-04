package com.dimachine.core.annotation;

import java.lang.annotation.*;

@Inherited
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Scheduled {

    long initialDelay() default -1;

    long fixedRate() default -1;
}
