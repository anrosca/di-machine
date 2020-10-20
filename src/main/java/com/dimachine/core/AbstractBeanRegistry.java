package com.dimachine.core;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractBeanRegistry implements BeanRegistry {
    protected final Map<BeanDefinition, Object> singletonBeans = new ConcurrentHashMap<>();
    protected final Set<BeanDefinition> beanDefinitions = Collections.newSetFromMap(new ConcurrentHashMap<>());
    protected final DefaultObjectFactory objectFactory = new DefaultObjectFactory();
    protected final Set<String> beanNames = new HashSet<>();

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
}
