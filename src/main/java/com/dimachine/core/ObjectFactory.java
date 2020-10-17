package com.dimachine.core;

public interface ObjectFactory {
    Object instantiate(Class<?> clazz, DefaultBeanFactory beanFactory);
}
