package com.dimachine.core.proxy;

public interface ProxyFactory {

    Object makeProxy(ProxyTraits proxyTraits);

    Class<?> makeProxyClass(ProxyTraits proxyTraits);
}
