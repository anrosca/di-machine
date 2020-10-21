package com.dimachine.core.postprocessor;

import com.dimachine.core.BeanInitialisationException;
import com.dimachine.core.BeanPostProcessor;
import com.dimachine.core.Order;
import com.dimachine.core.annotation.Ordered;
import com.dimachine.core.annotation.PostConstruct;
import com.dimachine.core.util.ReflectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Ordered(Order.LEAST_PRECEDENCE)
public class PostConstructAnnotationBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialisation(Object bean, String beanName) {
        Class<?> beanClass = bean.getClass();
        for (Method method : ReflectionUtils.getDeclaredMethods(beanClass)) {
            if (method.isAnnotationPresent(PostConstruct.class)) {
                if (method.getParameterCount() != 0) {
                    throw new BeanInitialisationException("Failed to initialise bean " + bean.getClass() +
                            ". Expected init method " + method.getName() + " to have no parameters");
                }
                invokeMethod(bean, method);
            }
        }
        return bean;
    }

    protected void invokeMethod(Object bean, Method method) {
        try {
            method.setAccessible(true);
            method.invoke(bean);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new BeanInitialisationException("Initialisation of bean " + bean.getClass() + " failed", e);
        }
    }
}
