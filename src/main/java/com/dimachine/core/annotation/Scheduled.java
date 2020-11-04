package com.dimachine.core.annotation;

import java.lang.annotation.*;

@Inherited
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Scheduled {

    String cron() default "";

    long initialDelay() default -1;

    String initialDelayString() default "";

    long fixedRate() default -1;

    String fixedRateString() default "";

    long fixedDelay() default -1;

    String fixedDelayString() default "";
}
