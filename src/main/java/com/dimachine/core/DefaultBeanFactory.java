package com.dimachine.core;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultBeanFactory implements BeanFactory, BeanRegistry {
    private final Map<BeanDefinition, Object> singletonBeans = new ConcurrentHashMap<>();
    private final List<BeanDefinition> beanDefinitions = Collections.synchronizedList(new ArrayList<>());
    private final ClasspathScanner classpathScanner;
    private final DefaultObjectFactory objectFactory = new DefaultObjectFactory();

    public DefaultBeanFactory(String...packagesToScan) {
        this.classpathScanner = new ClasspathScanner(packagesToScan);
    }

    @Override
    public Object getBean(String name) {
        return singletonBeans.entrySet()
                .stream()
                .filter(entry -> entry.getKey().getBeanName().equals(name))
                .findFirst()
                .map(Map.Entry::getValue)
                .orElseThrow(() -> new NoSuchBeanDefinitionException("No bean with name " + name + " found"));
    }

    @Override
    public <T> T getBean(String name, Class<T> clazz) {
        return null;
    }

    @Override
    public <T> T getBean(Class<T> clazz) {
        return singletonBeans.entrySet()
                .stream()
                .filter(entry -> entry.getKey().getBeanClass().equals(clazz))
                .findFirst()
                .map(Map.Entry::getValue)
                .map(clazz::cast)
                .orElseThrow(() -> new NoSuchBeanDefinitionException("No bean with of type " + clazz + " found"));
    }

    @Override
    public void registerBean(BeanDefinition beanDefinition) {
        beanDefinitions.add(beanDefinition);
        if (beanDefinition.isSingleton()) {
            singletonBeans.put(beanDefinition, instantiateSingleton(beanDefinition));
        }
    }

    @Override
    public void refresh() {
        Set<BeanDefinition> beanDefinitions = classpathScanner.scan();
        beanDefinitions.forEach(this::registerBean);
    }

    private Object instantiateSingleton(BeanDefinition beanDefinition) {
        return objectFactory.instantiate(beanDefinition.getBeanClass());
    }
}
