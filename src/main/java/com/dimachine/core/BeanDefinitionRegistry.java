package com.dimachine.core;

import java.util.List;
import java.util.Set;

public interface BeanDefinitionRegistry {
    void registerBeans(BeanDefinition... beanDefinitions);

    void registerBeans(List<BeanDefinition> beanDefinitions);

    Set<BeanDefinition> getBeanDefinitions();

    BeanDefinition getBeanDefinition(Class<?> beanClass);

    <T> boolean containsBeanDefinitionOfType(Class<T> clazz);
}
