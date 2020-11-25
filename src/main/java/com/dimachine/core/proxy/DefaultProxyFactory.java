package com.dimachine.core.proxy;

import com.dimachine.core.util.ReflectionUtils;
import javassist.util.proxy.MethodFilter;
import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.Proxy;

import java.lang.reflect.Method;

public class DefaultProxyFactory implements ProxyFactory {

    @Override
    public Object makeProxy(ProxyTraits proxyTraits) {
        javassist.util.proxy.ProxyFactory proxyFactory = new javassist.util.proxy.ProxyFactory();
        proxyFactory.setSuperclass(proxyTraits.getSuperClass());
        proxyFactory.setInterfaces(new Class<?>[]{com.dimachine.core.proxy.Proxy.class});
        Class<?> clazz = proxyFactory.createClass(new MethodFilterAdapter(proxyTraits.getMethodFilter()));
        Proxy proxy = (Proxy) ReflectionUtils.makeInstance(clazz, proxyTraits.getConstructorArguments());
        proxy.setHandler(new MethodHandlerAdapter(proxyTraits.getMethodInterceptor()));
        return proxy;
    }

    @Override
    public Class<?> makeProxyClass(ProxyTraits proxyTraits) {
        javassist.util.proxy.ProxyFactory proxyFactory = new javassist.util.proxy.ProxyFactory();
        proxyFactory.setSuperclass(proxyTraits.getSuperClass());
        proxyFactory.setHandler(new MethodHandlerAdapter(proxyTraits.getMethodInterceptor()));
        proxyFactory.setInterfaces(new Class<?>[]{com.dimachine.core.proxy.Proxy.class});
        return proxyFactory.createClass(new MethodFilterAdapter(proxyTraits.getMethodFilter()));
    }

    private static class MethodHandlerAdapter implements MethodHandler {
        private final MethodInterceptor invocationHandler;

        private MethodHandlerAdapter(MethodInterceptor invocationHandler) {
            this.invocationHandler = invocationHandler;
        }

        @Override
        public Object invoke(Object proxy, Method originalMethod, Method proxyMethod, Object[] args) throws Throwable {
            return invocationHandler.invoke(proxy, originalMethod, proxyMethod, args);
        }
    }

    private static class MethodFilterAdapter implements MethodFilter {
        private final ProxyMethodFilter methodFilter;

        private MethodFilterAdapter(ProxyMethodFilter methodFilter) {
            this.methodFilter = methodFilter;
        }

        @Override
        public boolean isHandled(Method method) {
            return methodFilter.isHandled(method);
        }
    }
}
