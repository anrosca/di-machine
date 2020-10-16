package com.dimachine.core;

public interface BeanRegistry {
    void registerBean(BeanDefinition beanDefinition);

    void refresh();
}
