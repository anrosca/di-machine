package com.dimachine.core.postprocessor;

import com.dimachine.core.*;
import com.dimachine.core.annotation.Async;
import com.dimachine.core.proxy.*;
import com.dimachine.core.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class AsyncAnnotationBeanPostProcessor implements BeanPostProcessor, DisposableBean {
    private final Map<String, Class<?>> asyncBeans = new HashMap<>();
    private volatile ExecutorService executorService;
    private final AtomicBoolean executorServiceInitialized = new AtomicBoolean();
    private final DefaultBeanFactory beanFactory;

    public AsyncAnnotationBeanPostProcessor(DefaultBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
        executorServiceInitialized.set(false);
    }

    public AsyncAnnotationBeanPostProcessor(DefaultBeanFactory beanFactory, ExecutorService executorService) {
        this.beanFactory = beanFactory;
        this.executorService = executorService;
        executorServiceInitialized.set(true);
    }

    @Override
    public Object postProcessBeforeInitialisation(Object bean, String beanName) {
        Class<?> beanClass = bean.getClass();
        for (Method method : ReflectionUtils.getDeclaredMethods(beanClass)) {
            if (method.isAnnotationPresent(Async.class)) {
                validateAsyncMethods(beanClass);
                initializeExecutorService();
                asyncBeans.put(beanName, beanClass);
                break;
            }
        }
        return bean;
    }

    private void initializeExecutorService() {
        if (executorServiceInitialized.compareAndSet(false, true)) {
            executorService = Executors.newScheduledThreadPool(1);
        }
    }

    private void validateAsyncMethods(Class<?> beanClass) {
        for (Method method : ReflectionUtils.getDeclaredMethods(beanClass)) {
            if (method.isAnnotationPresent(Async.class)) {
                if (method.getReturnType() != void.class) {
                    throw new InvalidAsyncMethodException("Method %s was expected to have void return type".formatted(
                            ReflectionUtils.makePrettyMethodSignature(beanClass, method)
                    ));
                }
            }
        }
    }

    @Override
    public Object postProcessAfterInitialisation(Object bean, String beanName) {
        if (asyncBeans.containsKey(beanName)) {
            ProxyFactory proxyFactory = new DefaultProxyFactory();
            ProxyTraits proxyTraits = ProxyTraits.builder()
                    .superClass(bean.getClass())
                    .methodInterceptor(new AsyncMethodInterceptor(bean, executorService))
                    .methodFilter(new IgnoreObjectMethodsMethodFilter())
                    .build();
            Class<?> proxyClass = proxyFactory.makeProxyClass(proxyTraits);
            ObjectFactory objectFactory = new DefaultObjectFactory();
            return objectFactory.instantiate(proxyClass, beanFactory);
        }
        return bean;
    }

    @Override
    public void destroy() {
        if (executorServiceInitialized.get()) {
            doDestroyExecutorService();
        }
    }

    private void doDestroyExecutorService() {
        try {
            executorService.shutdown();
            if (!executorService.awaitTermination(1, TimeUnit.MINUTES)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    private static class AsyncMethodInterceptor implements MethodInterceptor {
        private final Object target;
        private final ExecutorService executorService;

        public AsyncMethodInterceptor(Object target, ExecutorService executorService) {
            this.target = target;
            this.executorService = executorService;
        }

        @Override
        public Object invoke(Object proxyInstance, Method originalMethod, Method proxyMethod, Object[] args) {
            if (originalMethod.isAnnotationPresent(Async.class)) {
                executorService.execute(() -> ReflectionUtils.invokeMethod(target, originalMethod, args));
                return null;
            }
            return ReflectionUtils.invokeMethod(target, originalMethod, args);
        }
    }
}
