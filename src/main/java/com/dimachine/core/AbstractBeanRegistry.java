package com.dimachine.core;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractBeanRegistry implements BeanRegistry {
    protected final Map<BeanDefinition, Object> singletonBeans = new ConcurrentHashMap<>();
    protected final List<BeanDefinition> beanDefinitions = Collections.synchronizedList(new ArrayList<>());
    protected final DefaultObjectFactory objectFactory = new DefaultObjectFactory();

    @Override
    public void registerBeans(BeanDefinition...beanDefinitions) {
        this.beanDefinitions.addAll(Arrays.asList(beanDefinitions));
    }
}
