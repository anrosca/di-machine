package com.dimachine.core.env;

import com.dimachine.core.BeanFactory;
import com.dimachine.core.annotation.Value;

import java.lang.annotation.Annotation;

public class EnvironmentUtil {

    public static boolean isEnvironmentValue(Annotation[] annotations) {
        for (Annotation annotation : annotations) {
            if (Value.class == annotation.annotationType()) {
                return true;
            }
        }
        return false;
    }

    public static String resolveValue(Annotation[] annotations, BeanFactory beanFactory) {
        for (Annotation annotation : annotations) {
            if (Value.class == annotation.annotationType()) {
                Environment environment = beanFactory.getBean(Environment.class);
                return environment.resolvePlaceholder(((Value) annotation).value());
            }
        }
        return null;
    }
}
