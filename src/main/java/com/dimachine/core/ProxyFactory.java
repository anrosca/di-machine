package com.dimachine.core;

public interface ProxyFactory {
    Object proxyConfigurationClass(Object beanInstance, DefaultBeanFactory beanFactory);

    Object proxyConfigurationClass(Class<?> beanClass, Object[] constructorArguments, DefaultBeanFactory beanFactory);
}
