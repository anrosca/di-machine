package com.dimachine.core;

import com.dimachine.core.annotation.Bean;
import com.dimachine.core.scanner.AnnotationBeanDefinitionScanner;
import com.dimachine.core.util.ReflectionUtils;

import java.lang.reflect.Method;

public class JavaConfigObjectProvider implements ObjectProvider {
    private final Object configClassInstance;
    private final BeanDefinition beanDefinition;
    private final AnnotationBeanDefinitionScanner beanDefinitionScanner = new AnnotationBeanDefinitionScanner();

    public JavaConfigObjectProvider(Object configClassInstance, BeanDefinition beanDefinition) {
        this.configClassInstance = configClassInstance;
        this.beanDefinition = beanDefinition;
    }

    @Override
    public Object makeObject(BeanFactory beanFactory) {
        for (Method method : configClassInstance.getClass().getMethods()) {
            if (method.isAnnotationPresent(Bean.class)) {
                BeanDefinition currentBeanDefinition = beanDefinitionScanner.makeBeanDefinition(method);
                if (currentBeanDefinition.equals(beanDefinition)) {
                    return ReflectionUtils.invokeMethod(configClassInstance, method);
                }
            }
        }
        throw new BeanInitialisationException("");
    }
}
