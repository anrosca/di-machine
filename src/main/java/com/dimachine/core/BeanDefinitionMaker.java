package com.dimachine.core;

public sealed interface BeanDefinitionMaker permits DefaultBeanDefinitionMaker {
    BeanDefinition makeBeanDefinition(String className);
}
