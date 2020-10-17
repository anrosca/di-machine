package com.dimachine.core;

public class DefaultBeanNamer implements BeanNamer {
    @Override
    public String makeBeanName(String beanClassName) {
        String simpleName = beanClassName.substring(beanClassName.lastIndexOf(".") + 1);
        return Character.toLowerCase(simpleName.charAt(0)) + simpleName.substring(1);
    }
}
