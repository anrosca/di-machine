package com.dimachine.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class DefaultObjectFactory implements ObjectFactory {
    private static final Logger log = LoggerFactory.getLogger(DefaultObjectFactory.class);

    @Override
    public <T> T instantiate(Class<T> beanClass, DefaultBeanFactory beanFactory) {
        try {
            return tryInstantiate(beanClass, beanFactory);
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            log.error("Failed to instantiate singleton bean of type " + beanClass, e);
            throw new BeanInstantiationException("Failed to instantiate bean of type " + beanClass, e);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T tryInstantiate(Class<T> beanClass, DefaultBeanFactory beanFactory)
            throws InstantiationException, IllegalAccessException, InvocationTargetException {
        Constructor<?>[] declaredConstructors = beanClass.getDeclaredConstructors();
        Constructor<?> constructor = getGreediestParamConstructor(declaredConstructors, beanFactory, beanClass);
        if (constructor == null)
            throw new BeanCannotBeInstantiatedException("Bean of type " + beanClass + " cannot be instantiated " +
                    "because it has no default constructor");
        registerBeanDependencies(constructor.getParameterTypes(), beanFactory);
        constructor.setAccessible(true);
        Object[] constructorArguments = getConstructorArguments(constructor, beanFactory);
        return (T) constructor.newInstance(constructorArguments);
    }

    private void registerBeanDependencies(Class<?>[] beans, DefaultBeanFactory beanFactory) {
        for (Class<?> beanClass : beans) {
            if (!beanFactory.contains(beanClass)) {
                BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanClass);
                beanFactory.registerSingleton(beanDefinition, instantiate(beanDefinition.getRealBeanClass(), beanFactory));
            }
        }
    }

    private Object[] getConstructorArguments(Constructor<?> constructor, BeanFactory beanFactory) {
        Class<?>[] parameterTypes = constructor.getParameterTypes();
        Object[] arguments = new Object[parameterTypes.length];
        for (int i = 0; i < arguments.length; ++i) {
            Class<?> parameter = parameterTypes[i];
            arguments[i] = beanFactory.getBean(parameter);
        }
        return arguments;
    }

    private Constructor<?> getGreediestParamConstructor(Constructor<?>[] declaredConstructors,
                                                        BeanDefinitionRegistry beanDefinitionRegistry, Class<?> beanClass) {
        for (Constructor<?> constructor : declaredConstructors) {
            Class<?>[] parameterTypes = constructor.getParameterTypes();
            if (!isDefaultConstructor(constructor)) {
                if (canSatisfyParameters(beanDefinitionRegistry, parameterTypes)) {
                    checkForCycles(constructor, beanClass);
                    return constructor;
                }
            }
        }
        return getDefaultConstructor(declaredConstructors);
    }

    private boolean canSatisfyParameters(BeanDefinitionRegistry beanDefinitionRegistry, Class<?>[] parameterTypes) {
        boolean canSatisfyParameters = true;
        for (Class<?> parameterType : parameterTypes) {
            canSatisfyParameters &= beanDefinitionRegistry.containsBeanDefinitionOfType(parameterType);
        }
        return canSatisfyParameters;
    }

    private void checkForCycles(Constructor<?> constructor, Class<?> beanClass) {
        Class<?>[] constructorParameterTypes = constructor.getParameterTypes();
        for (Class<?> parameterClass : constructorParameterTypes) {
            Constructor<?>[] declaredConstructors = parameterClass.getDeclaredConstructors();
            for (Constructor<?> dependencyConstructor : declaredConstructors) {
                Class<?>[] dependenciesTypes = dependencyConstructor.getParameterTypes();
                for (Class<?> dependencyClass : dependenciesTypes) {
                    if (dependencyClass.equals(beanClass))
                        throw new BeanCurrentlyInCreationException("Circular dependency between " + beanClass + " and " + parameterClass);
                }
            }
        }
    }

    private boolean isDefaultConstructor(Constructor<?> constructor) {
        return constructor.getParameterTypes().length == 0;
    }

    private Constructor<?> getDefaultConstructor(Constructor<?>[] declaredConstructors) {
        for (Constructor<?> constructor : declaredConstructors) {
            Class<?>[] parameterTypes = constructor.getParameterTypes();
            if (parameterTypes.length == 0) {
                return constructor;
            }
        }
        return null;
    }
}
