package com.dimachine.core;

import com.dimachine.core.util.ReflectionUtils;
import javassist.util.proxy.MethodFilter;
import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.Proxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

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

    private final ScopeResolver scopeResolver = new ScopeResolver();

    @Override
    public Object proxyConfigurationClass(Object beanInstance, BeanFactory beanFactory) {
        javassist.util.proxy.ProxyFactory proxyFactory = new javassist.util.proxy.ProxyFactory();
        proxyFactory.setSuperclass(beanInstance.getClass());
        proxyFactory.setInterfaces(new Class<?>[] {com.dimachine.core.Proxy.class});
        Class<?> clazz = proxyFactory.createClass(new IgnoreObjectMethodsMethodFilter());
        Proxy proxy = (Proxy) ReflectionUtils.makeInstance(clazz);
        proxy.setHandler(new ConfigurationClassInvocationHandler(beanFactory, scopeResolver));
        return proxy;
    }

    private static class ConfigurationClassInvocationHandler implements MethodHandler {
        private final Logger log = LoggerFactory.getLogger(ConfigurationClassInvocationHandler.class);
        private final Object lock = new Object();
        private final Set<String> executedMethods = Collections.newSetFromMap(new ConcurrentHashMap<>());

        private final BeanFactory beanFactory;
        private final ScopeResolver scopeResolver;

        private ConfigurationClassInvocationHandler(BeanFactory beanFactory, ScopeResolver scopeResolver) {
            this.beanFactory = beanFactory;
            this.scopeResolver = scopeResolver;
        }

        @Override
        public Object invoke(Object self, Method thisMethod, Method proceed, Object[] args) throws Throwable {
            BeanScope beanScope = scopeResolver.resolveScope(thisMethod);
            if (isPrototype(beanScope)) {
                return proceed.invoke(self, args);
            } else {
                return handleSingletonMethodInvocation(self, thisMethod, proceed, args);
            }
        }

        private Object handleSingletonMethodInvocation(Object self, Method thisMethod, Method proceed, Object[] args) throws Exception {
            synchronized (lock) {
                String methodSignature = makeMethodSignature(self, proceed);
                if (!executedMethods.contains(methodSignature)) {
                    executedMethods.add(methodSignature);
                    return proceed.invoke(self, args);
                } else {
                    log.debug("@Bean method already invoked. Getting bean from BeanFactory instead");
                    return beanFactory.getBean(thisMethod.getReturnType());
                }
            }
        }

        private boolean isPrototype(BeanScope beanScope) {
            return beanScope == BeanScope.PROTOTYPE;
        }

        private String makeMethodSignature(Object self, Method proceed) {
            return proceed.getModifiers() + " " + proceed.getReturnType().getName() + " " +
                    self.getClass().getName() + "." + proceed.getName() +
                    "(" + Arrays.toString(proceed.getParameterTypes()) + ")";
        }
    }

    private static class IgnoreObjectMethodsMethodFilter implements MethodFilter {
        @Override
        public boolean isHandled(Method method) {
            return !bypassedMethods.contains(method.getName());
        }
    }
}
