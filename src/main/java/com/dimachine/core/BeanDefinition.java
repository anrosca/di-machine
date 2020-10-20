package com.dimachine.core;

public interface BeanDefinition {
    String getClassName();

    String getBeanName();

    BeanScope getBeanScope();

    boolean isSingleton();

    boolean isPrototype();

    Class<?> getRealBeanClass();

    Class<?> getBeanAssignableClass();

    ObjectProvider getObjectProvider();

    void setObjectProvider(ObjectProvider objectProvider);
}
