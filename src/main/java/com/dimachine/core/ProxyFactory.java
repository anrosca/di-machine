package com.dimachine.core;

public interface ProxyFactory {
    Object proxyConfigurationClass(Object beanInstance, BeanFactory beanFactory);
}
