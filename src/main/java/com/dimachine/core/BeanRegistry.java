package com.dimachine.core;

public interface BeanRegistry {
    void registerBean(BeanDefinition...beanDefinitions);

    void refresh();
}
