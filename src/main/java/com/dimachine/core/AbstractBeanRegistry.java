package com.dimachine.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractBeanRegistry implements BeanRegistry {
    protected final Map<BeanDefinition, Object> singletonBeans = new ConcurrentHashMap<>();
    protected final List<BeanDefinition> beanDefinitions = Collections.synchronizedList(new ArrayList<>());
    protected final DefaultObjectFactory objectFactory = new DefaultObjectFactory();

    @Override
    public void registerBean(BeanDefinition beanDefinition) {
        beanDefinitions.add(beanDefinition);
    }

    protected abstract Object instantiateSingleton(BeanDefinition beanDefinition);
}
