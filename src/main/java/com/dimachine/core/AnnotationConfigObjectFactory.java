package com.dimachine.core;

import com.dimachine.core.annotation.Bean;
import com.dimachine.core.scanner.AnnotationBeanDefinitionScanner;
import com.dimachine.core.scanner.BeanDefinitionScanner;
import com.dimachine.core.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

public class AnnotationConfigObjectFactory {
    private final DefaultBeanFactory beanFactory;
    private final BeanDefinitionScanner beanDefinitionScanner = new AnnotationBeanDefinitionScanner();

    public AnnotationConfigObjectFactory(DefaultBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public void instantiateSingletonsFromConfigClass(Object configBeanInstance, Class<?> originalConfigClass) {
        Set<Method> unresolvedMethods = new HashSet<>();
        for (Method method : originalConfigClass.getMethods()) {
            if (method.isAnnotationPresent(Bean.class)) {
                BeanDefinition beanDefinition = beanDefinitionScanner.makeBeanDefinition(method);
                setBeanDefinitionObjectFactory(configBeanInstance, beanDefinition);
                beanFactory.registerBeans(beanDefinition);
                if (beanDefinition.isSingleton()) {
                    Class<?>[] parameterTypes = method.getParameterTypes();
                    if (canSatisfyDependencies(parameterTypes)) {
                        registerSingleton(configBeanInstance, method);
                    } else {
                        unresolvedMethods.add(method);
                    }
                }
            }
        }
        processUnresolvedMethods(configBeanInstance, unresolvedMethods);
    }

    protected void setBeanDefinitionObjectFactory(Object configBeanInstance, BeanDefinition beanDefinition) {
        if (beanDefinition.isPrototype()) {
            ObjectProvider objectProvider = new JavaConfigObjectProvider(configBeanInstance, beanDefinition);
            beanDefinition.setObjectProvider(objectProvider);
        }
    }

    protected void processUnresolvedMethods(Object configBeanInstance, Set<Method> unresolvedMethods) {
        for (Method method : unresolvedMethods) {
            Class<?>[] parameterTypes = method.getParameterTypes();
            if (canSatisfyDependencies(parameterTypes)) {
                registerSingleton(configBeanInstance, method);
            }
        }
    }

    protected void registerSingleton(Object configBeanInstance, Method method) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        Object[] dependencies = resolveDependencies(parameterTypes);
        Object newBeanInstance = ReflectionUtils.invokeMethod(configBeanInstance, method, dependencies);
        BeanDefinition beanDefinition = beanDefinitionScanner.makeBeanDefinition(method);
        beanFactory.registerSingleton(beanDefinition, newBeanInstance);
    }

    private Object[] resolveDependencies(Class<?>[] parameterTypes) {
        Object[] dependencies = new Object[parameterTypes.length];
        int index = 0;
        for (Class<?> dependencyType : parameterTypes) {
            dependencies[index++] = beanFactory.getBean(dependencyType);
        }
        return dependencies;
    }

    private boolean canSatisfyDependencies(Class<?>[] parameterTypes) {
        boolean canSatisfyDependencies = true;
        for (Class<?> dependencyType : parameterTypes) {
            canSatisfyDependencies &= beanFactory.contains(dependencyType);
        }
        return canSatisfyDependencies;
    }
}
