package com.dimachine.core;

public interface BeanDefinition {
    String getClassName();

    String getBeanName();

    BeanScope getBeanScope();

    boolean isSingleton();

    Class<?> getBeanClass();
}
