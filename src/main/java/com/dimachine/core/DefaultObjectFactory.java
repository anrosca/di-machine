package com.dimachine.core;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

public class DefaultObjectFactory implements ObjectFactory {
    @Override
    public Object instantiate(Class<?> clazz, BeanFactory beanFactory) {
        try {
            Constructor<?>[] declaredConstructors = clazz.getDeclaredConstructors();
            Constructor<?> constructor = getGreediestParamConstructor(declaredConstructors, beanFactory, clazz);
            constructor.setAccessible(true);
            Object[] constructorArguments = getConstructorArguments(constructor, beanFactory);
            return constructor.newInstance(constructorArguments);
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new RuntimeException(e);
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
