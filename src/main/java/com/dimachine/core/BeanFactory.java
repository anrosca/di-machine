package com.dimachine.core;

public interface BeanFactory {

    Object getBean(String name);

    <T> T getBean(String name, Class<T> clazz);

    <T> T getBean(Class<T> clazz);
}
