package com.dimachine.core;

public interface BeanPostProcessor {
    default Object postProcessBeforeInitialisation(Object bean, String beanName) {
        return bean;
    }

    default Object postProcessAfterInitialisation(Object bean, String beanName) {
        return bean;
    }
}
