package com.dimachine.core;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractBeanDefinitionRegistry implements BeanDefinitionRegistry {
    protected final Set<BeanDefinition> beanDefinitions = Collections.newSetFromMap(new ConcurrentHashMap<>());
    protected final Set<String> beanNames = Collections.newSetFromMap(new ConcurrentHashMap<>());

    @Override
    public void registerBeans(BeanDefinition... beanDefinitions) {
        registerBeans(Arrays.asList(beanDefinitions));
    }

    @Override
    public void registerBeans(List<BeanDefinition> beanDefinitions) {
        for (BeanDefinition beanDefinition : beanDefinitions) {
            this.beanDefinitions.add(beanDefinition);
            boolean addedNewBeanName = this.beanNames.add(beanDefinition.getBeanName());
            if (!addedNewBeanName) {
                throw new DuplicateBeanNameException(beanDefinition.getBeanName() +
                        " is already present in BeanFactory. Bean names should be unique.");
            }
        }
    }

    @Override
    public <T> boolean containsBeanDefinitionOfType(Class<T> clazz) {
        return beanDefinitions.stream()
                .anyMatch(beanDefinition -> clazz.isAssignableFrom(beanDefinition.getBeanAssignableClass()));
    }

    @Override
    public Optional<BeanDefinition> getBeanDefinition(Class<?> beanClass) {
        return beanDefinitions.stream()
                .filter(beanDefinition -> beanClass.isAssignableFrom(beanDefinition.getBeanAssignableClass()))
                .findFirst();
    }

    @Override
    public Optional<BeanDefinition> getBeanDefinition(String beanName) {
        return beanDefinitions.stream()
                .filter(beanDefinition -> beanName.equals(beanDefinition.getBeanName()))
                .findFirst();
    }

    @Override
    public Set<BeanDefinition> getBeanDefinitions() {
        return Set.copyOf(beanDefinitions);
    }
}
