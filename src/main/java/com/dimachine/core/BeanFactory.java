package com.dimachine.core;

import java.util.List;
import java.util.Map;

public interface BeanFactory extends AutoCloseable {

    void register(Class<?>... configurationClasses);

    Object getBean(String name);

    <T> T getBean(String name, Class<T> beanClass);

    <T> T getBean(Class<T> beanClass);

    boolean contains(String name);

    <T> boolean contains(Class<T> beanClass);

    <T> boolean containsSingleton(Class<T> singletonClass);

    void registerSingleton(BeanDefinition beanDefinition, Object instance);

    <T> List<T> getAllBeansOfType(Class<T> clazz);

    <T> Map<String, T> getBeansMapOfType(Class<T> clazz);

    void refresh();
}
