package com.dimachine.core;

import com.dimachine.core.annotation.Bean;
import com.dimachine.core.scanner.AnnotationBeanDefinitionScanner;
import com.dimachine.core.util.ReflectionUtils;

import java.lang.reflect.Method;

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
    public Object makeObject(BeanFactory beanFactory) {
        for (Method method : originalConfigClass.getMethods()) {
            if (method.isAnnotationPresent(Bean.class)) {
                BeanDefinition currentBeanDefinition = beanDefinitionScanner.makeBeanDefinition(method);
                if (currentBeanDefinition.equals(beanDefinition)) {
                    BeanParameterResolver parameterResolver = new BeanParameterResolver(beanFactory);
                    return ReflectionUtils.invokeMethod(configClassInstance, method, parameterResolver.resolve(method));
                }
            }
        }
        throw new BeanInitialisationException("Bean with name " + beanDefinition.getBeanName() +
                " with class " + beanDefinition.getBeanAssignableClass() +
                " could not be instantiated. @Bean method missing.");
    }
}
