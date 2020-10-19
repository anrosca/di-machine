package com.dimachine.core;

import com.dimachine.core.util.ReflectionUtils;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.Set;

public class DefaultProxyFactory implements ProxyFactory {
    private static final Set<String> bypassedMethods = Set.of(
            "equals",
            "hashCode",
            "wait",
            "notify",
            "notifyAll",
            "getClass",
            "finalize",
            "toString",
            "clone"
    );

    @Override
    public Object proxyConfigurationClass(Object beanInstance, BeanDefinition beanDefinition, BeanFactory beanFactory) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(beanInstance.getClass());
        enhancer.setInterfaces(new Class<?>[]{Proxy.class});
        enhancer.setCallback(new ConfigurationClassInvocationHandler(beanInstance, beanFactory));
        return enhancer.create();
    }

    private static class ConfigurationClassInvocationHandler implements MethodInterceptor {
        private final Object beanInstance;
        private final BeanFactory beanFactory;

        private ConfigurationClassInvocationHandler(Object beanInstance, BeanFactory beanFactory) {
            this.beanInstance = beanInstance;
            this.beanFactory = beanFactory;
        }

        @Override
        public Object intercept(Object proxy, Method method, Object[] arguments, MethodProxy methodProxy) throws Throwable {
            if (shouldBypassMethod(method)) {
                return ReflectionUtils.invokeMethod(beanInstance, method, arguments);
            }
            Object returnedResult = methodProxy.invoke(beanInstance, arguments);
            return returnedResult;
        }

        private boolean shouldBypassMethod(Method method) {
            return bypassedMethods.contains(method.getName());
        }
    }
}
