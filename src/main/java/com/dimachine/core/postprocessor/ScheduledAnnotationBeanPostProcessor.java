package com.dimachine.core.postprocessor;

import com.dimachine.core.BeanFactory;
import com.dimachine.core.BeanPostProcessor;
import com.dimachine.core.DisposableBean;
import com.dimachine.core.annotation.Scheduled;
import com.dimachine.core.concurrent.MethodExecutingRunnable;
import com.dimachine.core.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class ScheduledAnnotationBeanPostProcessor implements BeanPostProcessor, DisposableBean {
    private final Map<String, Object> scheduledBeans = new HashMap<>();
    private final BeanFactory beanFactory;
    private volatile ScheduledExecutorService executorService;
    private final AtomicBoolean executorServiceInitialized = new AtomicBoolean();

    public ScheduledAnnotationBeanPostProcessor(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    ScheduledAnnotationBeanPostProcessor(BeanFactory beanFactory, ScheduledExecutorService executorService) {
        this.beanFactory = beanFactory;
        this.executorService = executorService;
        executorServiceInitialized.set(true);
    }

    @Override
    public Object postProcessBeforeInitialisation(Object bean, String beanName) {
        Class<?> beanClass = bean.getClass();
        for (Method method : ReflectionUtils.getDeclaredMethods(beanClass)) {
            if (method.isAnnotationPresent(Scheduled.class)) {
                validateScheduledMethod(method);
                scheduledBeans.put(beanName, bean);
                initializeExecutorService();
                break;
            }
        }
        return bean;
    }

    private void validateScheduledMethod(Method method) {
        if (method.getParameterCount() != 0) {
            throw new InvalidScheduledMethodException("Invalid @Scheduled method " + makePrettyMethodSignature(method)
                    + ". @Scheduled methods should have no parameters");
        }
    }

    private String makePrettyMethodSignature(Method method) {
        String parameterList = Arrays.stream(method.getParameterTypes())
                .map(Class::getSimpleName)
                .collect(Collectors.joining(","));
        return method.getDeclaringClass().getSimpleName() + "." + method.getName() + "(" + parameterList + ")";
    }

    @Override
    public Object postProcessAfterInitialisation(Object bean, String beanName) {
        if (scheduledBeans.containsKey(beanName)) {
            Class<?> beanClass = scheduledBeans.get(beanName).getClass();
            for (Method method : ReflectionUtils.getDeclaredMethods(beanClass)) {
                if (method.isAnnotationPresent(Scheduled.class)) {
                    scheduleMethod(beanName, method);
                }
            }
        }
        return bean;
    }

    private void scheduleMethod(String beanName, Method method) {
        Scheduled scheduled = method.getAnnotation(Scheduled.class);
        long initialDelay = scheduled.initialDelay();
        long delay = scheduled.fixedRate();
        Runnable runnable = new MethodExecutingRunnable(method, beanName, beanFactory);
        executorService.schedule(runnable, initialDelay, TimeUnit.MILLISECONDS);
    }

    @Override
    public void destroy() {
        if (executorServiceInitialized.get()) {
            doShutdownExecutorService();
        }
    }

    private void doShutdownExecutorService() {
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

    private void initializeExecutorService() {
        if (executorServiceInitialized.compareAndSet(false, true)) {
            executorService = Executors.newScheduledThreadPool(1);
        }
    }
}
