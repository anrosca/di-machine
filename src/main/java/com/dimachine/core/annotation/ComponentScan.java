package com.dimachine.core.annotation;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Repeatable(ComponentScans.class)
public @interface ComponentScan {
    String[] value() default {};

    String[] basePackages() default {};

    Class<?>[] basePackageClasses() default {};

    ComponentScan.Filter[] includeFilters() default {};

    ComponentScan.Filter[] excludeFilters() default {};

    @interface Filter {
        Class<?>[] classes() default {};

        FilterType type();

        String[] pattern() default {};
    }

    enum FilterType {
        ANNOTATION,
        ASSIGNABLE_TYPE,
        CUSTOM,
        REGEX;
    }
}
