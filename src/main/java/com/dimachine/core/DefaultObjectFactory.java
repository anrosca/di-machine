package com.dimachine.core;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class DefaultObjectFactory implements ObjectFactory {
    @Override
    public Object instantiate(Class<?> clazz, DefaultBeanFactory beanFactory) {
        try {
            Constructor<?>[] declaredConstructors = clazz.getDeclaredConstructors();
            Constructor<?> constructor = getGreediestParamConstructor(declaredConstructors, beanFactory, clazz);
            registerBeanDependencies(constructor.getParameterTypes(), beanFactory);
            constructor.setAccessible(true);
            Object[] constructorArguments = getConstructorArguments(constructor, beanFactory);
            return constructor.newInstance(constructorArguments);
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private void registerBeanDependencies(Class<?>[] beans, DefaultBeanFactory beanFactory) {
        for (Class<?> beanClass : beans) {
            if (!beanFactory.contains(beanClass)) {
                BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanClass);
                beanFactory.registerSingleton(beanDefinition, beanFactory.instantiateSingleton(beanDefinition));
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

    private Constructor<?> getGreediestParamConstructor(Constructor<?>[] declaredConstructors, BeanFactory beanFactory, Class<?> beanClass) {
        for (Constructor<?> constructor : declaredConstructors) {
            Class<?>[] parameterTypes = constructor.getParameterTypes();
            if (!isDefaultConstructor(constructor)) {
                boolean canSatisfyParameters = true;
                for (Class<?> parameterType : parameterTypes) {
                    canSatisfyParameters &= beanFactory.containsBeanDefinitionOfType(parameterType);
                }
                if (canSatisfyParameters) {
                    checkForCycles(constructor, beanClass);
                    return constructor;
                }
            }
        }
        return detDefaultConstructor(declaredConstructors);
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

    private Constructor<?> detDefaultConstructor(Constructor<?>[] declaredConstructors) {
        for (Constructor<?> constructor : declaredConstructors) {
            Class<?>[] parameterTypes = constructor.getParameterTypes();
            if (parameterTypes.length == 0) {
                return constructor;
            }
        }
        return null;
    }
}
