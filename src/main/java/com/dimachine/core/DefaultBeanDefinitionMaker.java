package com.dimachine.core;

public class DefaultBeanDefinitionMaker implements BeanDefinitionMaker {
    private final BeanNamer beanNamer = new DefaultBeanNamer();
    private final ScopeResolver scopeResolver = new ScopeResolver();

    @Override
    public BeanDefinition makeBeanDefinition(String className) {
        return SimpleBeanDefinition.builder()
                .className(className)
                .beanName(makeBeanName(className))
                .scope(scopeResolver.resolveScope(className))
                .build();
    }

    private String makeBeanName(String className) {
        return beanNamer.makeBeanName(className);
    }
}
