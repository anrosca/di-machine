package com.dimachine.core;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface BeanDefinitionRegistry {
    void registerBeans(BeanDefinition... beanDefinitions);

    void registerBeans(List<BeanDefinition> beanDefinitions);

    Set<BeanDefinition> getBeanDefinitions();

    Optional<BeanDefinition> getBeanDefinition(Class<?> beanClass);

    Optional<BeanDefinition> getBeanDefinition(String beanName);

    <T> boolean containsBeanDefinitionOfType(Class<T> clazz);
}
