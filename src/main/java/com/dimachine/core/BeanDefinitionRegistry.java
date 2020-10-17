package com.dimachine.core;

public interface BeanDefinitionRegistry {

    <T> boolean contains(Class<T> clazz);

    <T> boolean containsBeanDefinitionOfType(Class<T> clazz);

    <T> BeanDefinition getBeanDefinition(Class<T> clazz);
}
