package com.dimachine.core.postprocessor;

import com.dimachine.core.BeanPostProcessor;
import com.dimachine.core.annotation.Value;
import com.dimachine.core.env.Environment;
import com.dimachine.core.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Objects;

public class ValueAnnotationBeanPostProcessor implements BeanPostProcessor {
    private final Environment environment;

    public ValueAnnotationBeanPostProcessor(Environment environment) {
        this.environment = environment;
    }

    @Override
    public Object postProcessBeforeInitialisation(Object bean, String beanName) {
        Class<?> beanClass = bean.getClass();
        setFields(bean, beanClass);
        setMethods(bean, beanClass);
        return bean;
    }

    private void setMethods(Object bean, Class<?> beanClass) {
        for (Method method : ReflectionUtils.getDeclaredMethods(beanClass)) {
            if (method.isAnnotationPresent(Value.class)) {
                Value value = method.getAnnotation(Value.class);
                String resolvedValue = environment.resolvePlaceholder(value.value());
                ReflectionUtils.invokeMethod(bean, method, Objects.requireNonNullElseGet(resolvedValue, value::value));
            }
        }
    }

    private void setFields(Object bean, Class<?> beanClass) {
        for (Field field : ReflectionUtils.getDeclaredFields(beanClass)) {
            if (field.isAnnotationPresent(Value.class)) {
                Value value = field.getAnnotation(Value.class);
                String resolvedValue = environment.resolvePlaceholder(value.value());
                ReflectionUtils.setField(bean, field,
                        Objects.requireNonNullElseGet(resolvedValue, value::value));
            }
        }
    }
}
