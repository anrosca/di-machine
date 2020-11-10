package com.dimachine.core.postprocessor;

import com.dimachine.core.BeanPostProcessor;
import com.dimachine.core.annotation.Value;
import com.dimachine.core.env.Environment;
import com.dimachine.core.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Objects;

public class ValueAnnotationBeanPostProcessor implements BeanPostProcessor {
    private final Environment environment;

    public ValueAnnotationBeanPostProcessor(Environment environment) {
        this.environment = environment;
    }

    @Override
    public Object postProcessBeforeInitialisation(Object bean, String beanName) {
        Class<?> beanClass = bean.getClass();
        for (Field field : ReflectionUtils.getDeclaredFields(beanClass)) {
            if (field.isAnnotationPresent(Value.class)) {
                Value value = field.getAnnotation(Value.class);
                String resolvedValue = environment.resolvePlaceholder(value.value());
                ReflectionUtils.setField(bean, field,
                        Objects.requireNonNullElseGet(resolvedValue, value::value));
            }
        }
        return bean;
    }
}
