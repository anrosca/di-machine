package com.dimachine.core;

public interface ObjectFactory {
    <T> T instantiate(Class<T> clazz, DefaultBeanFactory beanFactory);
}
