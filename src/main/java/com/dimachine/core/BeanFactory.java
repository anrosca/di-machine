package com.dimachine.core;

public interface BeanFactory {

    Object getBean(String name);

    <T> T getBean(String name, Class<T> clazz);

    <T> T getBean(Class<T> clazz);

    boolean contains(String name);

    <T> boolean contains(Class<T> clazz);

    <T> boolean containsBeanDefinitionOfType(Class<T> clazz);

    BeanDefinition getBeanDefinition(Class<?> beanClass);

    void registerSingleton(BeanDefinition beanDefinition, Object instance);
}
