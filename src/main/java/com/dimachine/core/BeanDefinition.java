package com.dimachine.core;

public interface BeanDefinition {
    String getClassName();

    String getBeanName();

    Scope getBeanScope();

    boolean isSingleton();

    Class<?> getBeanClass();
}
