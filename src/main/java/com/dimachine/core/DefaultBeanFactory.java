package com.dimachine.core;

import java.lang.reflect.Constructor;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class DefaultBeanFactory extends AbstractBeanRegistry implements BeanFactory, BeanRegistry {
    private final ClasspathScanner classpathScanner;

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
        return singletonBeans.entrySet()
                .stream()
                .filter(beanEntry -> beanEntry.getKey().getBeanName().equals(name))
                .filter(beanEntry -> clazz.isAssignableFrom(beanEntry.getKey().getBeanClass()))
                .map(Map.Entry::getValue)
                .findFirst()
                .map(clazz::cast)
                .orElseThrow(() -> new NoSuchBeanDefinitionException("No bean with name " + name + " and type " + clazz + " found"));
    }

    @Override
    public <T> T getBean(Class<T> clazz) {
        return singletonBeans.entrySet()
                .stream()
                .filter(entry -> clazz.isAssignableFrom(entry.getKey().getBeanClass()))
                .findFirst()
                .map(Map.Entry::getValue)
                .map(clazz::cast)
                .orElseThrow(() -> new NoSuchBeanDefinitionException("No bean with of type " + clazz + " found"));
    }

    @Override
    public boolean contains(String beanName) {
        return singletonBeans.entrySet()
                .stream()
                .anyMatch(beanEntry -> beanEntry.getKey().getBeanName().equals(beanName));
    }

    @Override
    public <T> boolean contains(Class<T> clazz) {
        return singletonBeans.entrySet()
                .stream()
                .anyMatch(beanEntry -> clazz.isAssignableFrom(beanEntry.getKey().getBeanClass()));
    }

    @Override
    public void refresh() {
        List<BeanDefinition> beanDefinitions = classpathScanner.scan();
        sortBeanDefinitionsByConstructorGreediness(beanDefinitions);
        beanDefinitions.forEach(this::registerBean);
    }

    private void sortBeanDefinitionsByConstructorGreediness(List<BeanDefinition> beanDefinitions) {
        beanDefinitions.sort(new Comparator<BeanDefinition>() {
            @Override
            public int compare(BeanDefinition first, BeanDefinition second) {
                Class<?> firstBeanClass = first.getBeanClass();
                Class<?> secondBeanClass = second.getBeanClass();
                int firstGreediestConstructor = getGreediestConstructorParameterCount(firstBeanClass);
                int secondGreediestConstructor = getGreediestConstructorParameterCount(secondBeanClass);
                return Integer.compare(firstGreediestConstructor, secondGreediestConstructor);
            }

            private int getGreediestConstructorParameterCount(Class<?> clazz) {
                int parameterCount = 0;
                Constructor<?>[] declaredConstructors = clazz.getDeclaredConstructors();
                for (Constructor<?> constructor : declaredConstructors) {
                    if (constructor.getParameterTypes().length > parameterCount) {
                        parameterCount = constructor.getParameterTypes().length;
                    }
                }
                return parameterCount;
            }
        });
    }

    protected Object instantiateSingleton(BeanDefinition beanDefinition) {
        return objectFactory.instantiate(beanDefinition.getBeanClass(), this);
    }
}
