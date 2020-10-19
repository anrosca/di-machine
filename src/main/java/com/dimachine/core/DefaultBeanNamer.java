package com.dimachine.core;

import com.dimachine.core.annotation.Bean;

import java.lang.reflect.Method;

public class DefaultBeanNamer implements BeanNamer {

    @Override
    public String makeBeanName(String beanClassName) {
        String simpleName = beanClassName.substring(beanClassName.lastIndexOf(".") + 1);
        return Character.toLowerCase(simpleName.charAt(0)) + simpleName.substring(1);
    }

    @Override
    public String makeBeanName(Method method, Bean beanAnnotation) {
        String configuredBeanName = getAnnotationNameAttribute(beanAnnotation).trim();
        if (!configuredBeanName.isEmpty()) {
            return configuredBeanName;
        }
        return method.getName();
    }

    protected String getAnnotationNameAttribute(Bean beanAnnotation) {
        return beanAnnotation != null ? beanAnnotation.name() : "";
    }
}
