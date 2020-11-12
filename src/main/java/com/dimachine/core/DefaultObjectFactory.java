package com.dimachine.core;

import com.dimachine.core.annotation.Configuration;
import com.dimachine.core.annotation.Value;
import com.dimachine.core.env.EnvironmentUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class DefaultObjectFactory implements ObjectFactory {
    private static final Logger log = LoggerFactory.getLogger(DefaultObjectFactory.class);

    private final ProxyFactory proxyFactory = new DefaultProxyFactory();

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
        registerBeanDependencies(constructor, beanFactory);
        constructor.setAccessible(true);
        Object[] constructorArguments = getConstructorArguments(constructor, beanFactory);
        if (isConfigurationBean(beanClass) && shouldProxyConfigClass(beanClass)) {
            return (T) proxyFactory.proxyConfigurationClass(beanClass, constructorArguments, beanFactory);
        }
        return (T) constructor.newInstance(constructorArguments);
    }

    private void registerBeanDependencies(Constructor<?> constructor, DefaultBeanFactory beanFactory) {
        Class<?>[] beans = constructor.getParameterTypes();
        Annotation[][] parameterAnnotations = constructor.getParameterAnnotations();
        int parameterIndex = 0;
        for (Class<?> beanClass : beans) {
            Annotation[] annotations = parameterAnnotations[parameterIndex++];
            if (!beanFactory.contains(beanClass) && !EnvironmentUtil.isEnvironmentValue(annotations)) {
                BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanClass)
                        .orElseThrow(() -> new NoSuchBeanDefinitionException("No bean definition of type " + beanClass + " found"));
                beanFactory.registerSingleton(beanDefinition, instantiate(beanDefinition.getRealBeanClass(), beanFactory));
            }
        }
    }

    private Object[] getConstructorArguments(Constructor<?> constructor, BeanFactory beanFactory) {
        Class<?>[] parameterTypes = constructor.getParameterTypes();
        Annotation[][] parameterAnnotations = constructor.getParameterAnnotations();
        Object[] arguments = new Object[parameterTypes.length];
        for (int i = 0; i < arguments.length; ++i) {
            Class<?> parameter = parameterTypes[i];
            Annotation[] annotations = parameterAnnotations[i];
            if (EnvironmentUtil.isEnvironmentValue(annotations)) {
                arguments[i] = EnvironmentUtil.resolveValue(annotations, beanFactory);
            } else {
                arguments[i] = beanFactory.getBean(parameter);
            }
        }
        return arguments;
    }

    private Constructor<?> getGreediestParamConstructor(Constructor<?>[] declaredConstructors,
                                                        BeanDefinitionRegistry registry, Class<?> beanClass) {
        for (Constructor<?> constructor : declaredConstructors) {
            Class<?>[] parameterTypes = constructor.getParameterTypes();
            Annotation[][] parameterAnnotations = constructor.getParameterAnnotations();
            if (!isDefaultConstructor(constructor)) {
                if (canSatisfyParameters(registry, parameterTypes, parameterAnnotations)) {
                    checkForCycles(constructor, beanClass);
                    return constructor;
                }
            }
        }
        return getDefaultConstructor(declaredConstructors);
    }

    private boolean canSatisfyParameters(BeanDefinitionRegistry registry, Class<?>[] parameterTypes, Annotation[][] parameterAnnotations) {
        boolean canSatisfyParameters = true;
        int parameterIndex = 0;
        for (Class<?> parameterType : parameterTypes) {
            Annotation[] annotations = parameterAnnotations[parameterIndex++];
            boolean isResolvable = registry.containsBeanDefinitionOfType(parameterType);
            isResolvable |= EnvironmentUtil.isEnvironmentValue(annotations);
            canSatisfyParameters &= isResolvable;
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
                        throw new BeanCurrentlyInCreationException("Circular dependency between " + beanClass +
                                " and " + parameterClass);
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

    private boolean isConfigurationBean(Class<?> beanClass) {
        return beanClass.isAnnotationPresent(Configuration.class);
    }

    private boolean shouldProxyConfigClass(Class<?> originalConfigClass) {
        Configuration configuration = originalConfigClass.getAnnotation(Configuration.class);
        return configuration.proxyBeanMethods();
    }
}
