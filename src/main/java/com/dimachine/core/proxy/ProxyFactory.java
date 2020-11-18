package com.dimachine.core.proxy;

public interface ProxyFactory {

    Object makeProxy(Class<?> beanClass, Object[] constructorArguments, MethodInterceptor methodInterceptor);

    Object makeProxy(Class<?> beanClass, MethodInterceptor methodInterceptor);

    Object makeProxy(ProxyTraits proxyTraits);
}
