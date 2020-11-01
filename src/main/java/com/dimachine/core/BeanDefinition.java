package com.dimachine.core;

import java.util.List;

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

    boolean isCompatibleWith(String beanName, Class<?> clazz);

    List<String> getAliases();

    boolean isCompatibleWith(String beanName);
}
