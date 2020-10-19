package com.dimachine.core;

import java.util.List;

public interface BeanRegistry {
    void registerBeans(BeanDefinition...beanDefinitions);

    void registerBeans(List<BeanDefinition> beanDefinitions);

    void refresh();
}
