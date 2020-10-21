package com.dimachine.core.postprocessor;

import com.dimachine.core.BeanPostProcessor;
import com.dimachine.core.DisposableBean;
import com.dimachine.core.annotation.PreDestroy;
import com.dimachine.core.util.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PreDestroyAnnotationBeanPostProcessor implements BeanPostProcessor, DisposableBean {
    private static final Logger log = LoggerFactory.getLogger(PreDestroyAnnotationBeanPostProcessor.class);

    private final Map<String, Object> disposableBeans = new ConcurrentHashMap<>();

    @Override
    public Object postProcessBeforeInitialisation(Object bean, String beanName) {
        Class<?> beanClass = bean.getClass();
        for (Method method : ReflectionUtils.getDeclaredMethods(beanClass)) {
            if (method.isAnnotationPresent(PreDestroy.class)) {
                disposableBeans.put(beanName, bean);
                break;
            }
        }
        return bean;
    }

    @Override
    public void destroy() {
        for (Object bean : disposableBeans.values()) {
            Class<?> beanClass = bean.getClass();
            for (Method method : ReflectionUtils.getDeclaredMethods(beanClass)) {
                if (method.isAnnotationPresent(PreDestroy.class)) {
                    checkIfMethodHasNoParameters(beanClass, method);
                    invokeMethodSwallowingException(bean, method);
                }
            }
        }
    }

    private void checkIfMethodHasNoParameters(Class<?> beanClass, Method method) {
        if (method.getParameterCount() != 0) {
            throw new InvalidDestroyMethodException("Invalid destroy method " + beanClass.getName() + "." + method +
                    ". Destroy method should have no parameters.");
        }
    }

    protected void invokeMethodSwallowingException(Object bean, Method method) {
        try {
            ReflectionUtils.invokeMethod(bean, method);
        } catch (Exception e) {
            log.error("Error while disposing bean: " + bean, e);
        }
    }
}
