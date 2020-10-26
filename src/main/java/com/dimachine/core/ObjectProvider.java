package com.dimachine.core;

public interface ObjectProvider {
    Object makeObject(DefaultBeanFactory beanFactory);

    boolean hasDependency(Class<?> beanClass);
}
