package com.dimachine.core;

import com.dimachine.core.annotation.Configuration;
import com.dimachine.core.annotation.Qualifier;
import com.dimachine.core.annotation.Value;
import com.dimachine.core.env.Environment;
import com.dimachine.core.proxy.*;
import com.dimachine.core.util.AnnotationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.function.Function;

public class DefaultObjectFactory implements ObjectFactory {
    private static final Logger log = LoggerFactory.getLogger(DefaultObjectFactory.class);

    private final ProxyFactory proxyFactory = new DefaultProxyFactory();

    @Override
    public <T> T instantiate(Class<T> beanClass, AbstractBeanFactory beanFactory) {
        try {
            return tryInstantiate(beanClass, beanFactory);
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            log.error("Failed to instantiate singleton bean of type " + beanClass, e);
            throw new BeanInstantiationException("Failed to instantiate bean of type " + beanClass, e);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T tryInstantiate(Class<T> beanClass, AbstractBeanFactory beanFactory)
            throws InstantiationException, IllegalAccessException, InvocationTargetException {
        Constructor<?> constructor = getGreediestParamConstructor(beanFactory, beanClass);
        registerBeanDependencies(constructor, beanFactory);
        constructor.setAccessible(true);
        Object[] constructorArguments = getConstructorArguments(constructor, beanFactory);
        if (isConfigurationBean(beanClass) && shouldProxyConfigClass(beanClass)) {
            return makeProxy(beanClass, beanFactory, constructorArguments);
        }
        return (T) constructor.newInstance(constructorArguments);
    }

    @SuppressWarnings("unchecked")
    private <T> T makeProxy(Class<T> beanClass, AbstractBeanFactory beanFactory, Object[] constructorArguments) {
        ProxyTraits proxyTraits = ProxyTraits.builder()
                .superClass(beanClass)
                .constructorArguments(constructorArguments)
                .methodInterceptor(new ConfigurationClassInvocationHandler(beanFactory))
                .methodFilter(new IgnoreObjectMethodsMethodFilter().and(new IncludeBeanMethodsProxyMethodFilter()))
                .build();
        return (T) proxyFactory.makeProxy(proxyTraits);
    }

    private void registerBeanDependencies(Constructor<?> constructor, AbstractBeanFactory beanFactory) {
        Class<?>[] beans = constructor.getParameterTypes();
        Annotation[][] parameterAnnotations = constructor.getParameterAnnotations();
        int parameterIndex = 0;
        for (Class<?> beanClass : beans) {
            Annotation[] annotations = parameterAnnotations[parameterIndex++];
            if (!beanFactory.contains(beanClass) && !isEnvironmentValue(annotations)) {
                BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanClass)
                        .orElseThrow(() -> new NoSuchBeanDefinitionException("No bean definition of type " + beanClass + " found"));
                beanFactory.registerSingleton(beanDefinition, instantiate(beanDefinition.getRealBeanClass(), beanFactory));
            }
        }
    }

    private boolean isEnvironmentValue(Annotation[] annotations) {
        return AnnotationUtils.containsAnnotation(annotations, Value.class);
    }

    private Object[] getConstructorArguments(Constructor<?> constructor, BeanFactory beanFactory) {
        Class<?>[] parameterTypes = constructor.getParameterTypes();
        Annotation[][] parameterAnnotations = constructor.getParameterAnnotations();
        Object[] arguments = new Object[parameterTypes.length];
        for (int i = 0; i < arguments.length; ++i) {
            Class<?> argumentType = parameterTypes[i];
            Annotation[] annotations = parameterAnnotations[i];
            arguments[i] = resolveArgumentValue(annotations, argumentType, beanFactory);
        }
        return arguments;
    }

    private Object resolveArgumentValue(Annotation[] annotations, Class<?> argumentType, BeanFactory beanFactory) {
        if (isEnvironmentValue(annotations)) {
            return resolveEnvironmentValue(beanFactory, annotations);
        } else if (isQualifiedName(annotations)) {
            return revolveQualifiedName(beanFactory, argumentType, annotations);
        } else {
            return beanFactory.getBean(argumentType);
        }
    }

    private Object revolveQualifiedName(BeanFactory beanFactory, Class<?> beanType, Annotation[] annotations) {
        String beanName = (String) AnnotationUtils.getAnnotationValue(annotations, Qualifier.class);
        return beanFactory.getBean(beanName, beanType);
    }

    private boolean isQualifiedName(Annotation[] annotations) {
        return AnnotationUtils.containsAnnotation(annotations, Qualifier.class);
    }

    private Object resolveEnvironmentValue(BeanFactory beanFactory, Annotation[] annotations) {
        String placeholder = (String) AnnotationUtils.getAnnotationValue(annotations, Value.class);
        Environment environment = beanFactory.getBean(Environment.class);
        return environment.resolvePlaceholder(placeholder);
    }

    private Constructor<?> getGreediestParamConstructor(AbstractBeanFactory beanFactory, Class<?> beanClass) {
        Constructor<?> constructor = tryGetGreediestParamConstructor(beanFactory, beanClass);
        if (constructor == null)
            throw new BeanCannotBeInstantiatedException("Bean of type " + beanClass + " cannot be instantiated " +
                    "because it has no default constructor");
        return constructor;
    }

    private Constructor<?> tryGetGreediestParamConstructor(AbstractBeanFactory beanFactory, Class<?> beanClass) {
        Constructor<?>[] declaredConstructors = beanClass.getDeclaredConstructors();
        Arrays.sort(declaredConstructors, Comparator.comparing((Function<Constructor<?>, Integer>) Constructor::getParameterCount).reversed());
        for (Constructor<?> constructor : declaredConstructors) {
            Class<?>[] parameterTypes = constructor.getParameterTypes();
            Annotation[][] parameterAnnotations = constructor.getParameterAnnotations();
            if (!isDefaultConstructor(constructor)) {
                if (canSatisfyParameters(beanFactory, parameterTypes, parameterAnnotations)) {
                    checkForCycles(constructor, beanClass);
                    return constructor;
                }
            }
        }
        return getDefaultConstructor(beanClass.getDeclaredConstructors());
    }

    private boolean canSatisfyParameters(AbstractBeanFactory beanFactory, Class<?>[] parameterTypes, Annotation[][] parameterAnnotations) {
        boolean canSatisfyParameters = true;
        int parameterIndex = 0;
        for (Class<?> parameterType : parameterTypes) {
            Annotation[] annotations = parameterAnnotations[parameterIndex++];
            boolean isResolvable = beanFactory.containsBeanDefinitionOfType(parameterType);
            isResolvable |= isEnvironmentValue(annotations);
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
