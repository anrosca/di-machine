package com.dimachine.core;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
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
    public BeanDefinition getBeanDefinition(Class<?> beanClass) {
        return beanDefinitions.stream()
                .filter(beanDefinition -> beanClass.isAssignableFrom(beanDefinition.getBeanAssignableClass()))
                .findFirst()
                .orElseThrow(() -> new NoSuchBeanDefinitionException("No bean definition of type " + beanClass + " found"));
    }

    @Override
    public Set<BeanDefinition> getBeanDefinitions() {
        return Set.copyOf(beanDefinitions);
    }
}
