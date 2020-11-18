package com.dimachine.core;

import com.dimachine.core.annotation.Bean;
import com.dimachine.core.scanner.AnnotationBeanDefinitionScanner;
import com.dimachine.core.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;

public class JavaConfigObjectProvider implements ObjectProvider {
    private final Object configClassInstance;
    private final BeanDefinition beanDefinition;
    private final Class<?> originalConfigClass;
    private final AnnotationBeanDefinitionScanner beanDefinitionScanner = new AnnotationBeanDefinitionScanner();

    public JavaConfigObjectProvider(Object configClassInstance, BeanDefinition beanDefinition, Class<?> originalConfigClass) {
        this.configClassInstance = configClassInstance;
        this.beanDefinition = beanDefinition;
        this.originalConfigClass = originalConfigClass;
    }

    @Override
    public Object makeObject(DefaultBeanFactory beanFactory) {
        for (Method method : originalConfigClass.getMethods()) {
            if (method.isAnnotationPresent(Bean.class)) {
                BeanDefinition currentBeanDefinition = beanDefinitionScanner.makeBeanDefinition(method);
                if (currentBeanDefinition.equals(beanDefinition)) {
                    checkForCycles(beanFactory, method);
                    BeanParameterResolver parameterResolver = new BeanParameterResolver(beanFactory);
                    return ReflectionUtils.invokeMethod(configClassInstance, method, parameterResolver.resolve(method));
                }
            }
        }
        throw new BeanInitializationException("Bean with name " + beanDefinition.getBeanName() +
                " with class " + beanDefinition.getBeanAssignableClass() +
                " could not be instantiated. @Bean method missing.");
    }

    private void checkForCycles(DefaultBeanFactory beanFactory, Method method) {
        if (isCyclicDependencyBetween(beanDefinition.getBeanAssignableClass(), method.getParameterTypes(), beanFactory)) {
            Optional<BeanDefinition> problematicDependency =
                    getProblematicDependency(beanDefinition.getBeanAssignableClass(), method.getParameterTypes(), beanFactory);
            throwInitializationException(problematicDependency);
        }
    }

    private void throwInitializationException(Optional<BeanDefinition> problematicDependency) {
        String problematicBeanName = problematicDependency.map(BeanDefinition::getBeanName).orElse("");
        Class<?> problematicBeanClass = problematicDependency.map(BeanDefinition::getBeanAssignableClass).orElse(null);
        throw new BeanCurrentlyInCreationException("Cyclic dependency between bean with name '" +
                beanDefinition.getBeanName() + "' and type " + beanDefinition.getBeanAssignableClass() +
                " and bean with name '" + problematicBeanName + "' with type " +
                problematicBeanClass);
    }

    @Override
    public boolean hasDependency(Class<?> beanClass) {
        for (Method method : originalConfigClass.getMethods()) {
            if (method.isAnnotationPresent(Bean.class)) {
                BeanDefinition currentBeanDefinition = beanDefinitionScanner.makeBeanDefinition(method);
                if (currentBeanDefinition.equals(beanDefinition)) {
                    return Arrays.stream(method.getParameterTypes())
                            .anyMatch(parameterType -> parameterType.isAssignableFrom(beanClass));
                }
            }
        }
        return false;
    }

    private Optional<BeanDefinition> getProblematicDependency(Class<?> beanClass, Class<?>[] beanDependencies, DefaultBeanFactory beanFactory) {
        for (Class<?> dependencyClass : beanDependencies) {
            if (hasCycle(beanClass, beanFactory, dependencyClass)) {
                return beanFactory.getBeanDefinition(dependencyClass);
            }
        }
        return Optional.empty();
    }

    private boolean isCyclicDependencyBetween(Class<?> beanClass, Class<?>[] beanDependencies, DefaultBeanFactory beanFactory) {
        for (Class<?> dependencyClass : beanDependencies) {
            if (hasCycle(beanClass, beanFactory, dependencyClass)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasCycle(Class<?> beanClass, DefaultBeanFactory beanFactory, Class<?> dependencyClass) {
        return beanFactory.getBeanDefinition(dependencyClass)
                .map(BeanDefinition::getObjectProvider)
                .map(objectProvider -> objectProvider.hasDependency(beanClass))
                .orElse(false);
    }
}
