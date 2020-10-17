package com.dimachine.core;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractBeanRegistry implements BeanRegistry {
    protected final Map<BeanDefinition, Object> singletonBeans = new ConcurrentHashMap<>();
    protected final List<BeanDefinition> beanDefinitions = Collections.synchronizedList(new ArrayList<>());
    private final DefaultObjectFactory objectFactory = new DefaultObjectFactory();

    @Override
    public void registerBean(BeanDefinition beanDefinition) {
        beanDefinitions.add(beanDefinition);
        if (beanDefinition.isSingleton()) {
            singletonBeans.put(beanDefinition, instantiateSingleton(beanDefinition));
        }
    }

    private Object instantiateSingleton(BeanDefinition beanDefinition) {
        return objectFactory.instantiate(beanDefinition.getBeanClass());
    }
}
