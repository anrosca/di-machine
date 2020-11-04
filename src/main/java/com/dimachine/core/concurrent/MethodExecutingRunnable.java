package com.dimachine.core.concurrent;

import com.dimachine.core.BeanFactory;
import com.dimachine.core.util.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

public class MethodExecutingRunnable implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(MethodExecutingRunnable.class);

    private final Method method;
    private final String beanName;
    private final BeanFactory beanFactory;

    public MethodExecutingRunnable(Method method, String beanName, BeanFactory beanFactory) {
        this.method = method;
        this.beanName = beanName;
        this.beanFactory = beanFactory;
    }

    @Override
    public void run() {
        log.debug("Invoking @Scheduled method {}.{}()", method.getDeclaringClass().getName(), method.getName());
        ReflectionUtils.invokeMethod(beanFactory.getBean(beanName), method);
    }
}
