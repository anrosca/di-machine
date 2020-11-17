package com.dimachine.core.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class AnnotationUtils {
    public static boolean containsAnnotation(Annotation[] annotations, Class<? extends Annotation> annotationClass) {
        for (Annotation annotation : annotations) {
            if (annotation.annotationType() == annotationClass)
                return true;
        }
        return false;
    }

    public static Object getAnnotationValue(Annotation[] annotations, Class<? extends Annotation> annotationClass) {
        for (Annotation annotation : annotations) {
            if (annotation.annotationType() == annotationClass) {
                Method method = getValueMethod(annotationClass);
                return ReflectionUtils.invokeMethod(annotation, method);
            }
        }
        return null;
    }

    private static Method getValueMethod(Class<? extends Annotation> annotation) {
        try {
            return annotation.getMethod("value");
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
