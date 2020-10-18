package com.dimachine.core;

public interface BeanRegistry {
    void registerBeans(BeanDefinition...beanDefinitions);

    void refresh();
}
