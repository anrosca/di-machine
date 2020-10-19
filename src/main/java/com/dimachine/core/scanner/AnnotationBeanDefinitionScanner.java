package com.dimachine.core.scanner;

import com.dimachine.core.*;
import com.dimachine.core.annotation.Bean;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class AnnotationBeanDefinitionScanner implements BeanDefinitionScanner {
    private final BeanNamer beanNamer = new DefaultBeanNamer();
    private final ScopeResolver scopeResolver = new ScopeResolver();

    @Override
    public List<BeanDefinition> scanBeanDefinitionsFrom(Class<?> configurationClass) {
        List<BeanDefinition> beanDefinitions = new ArrayList<>();
        for (Method method : configurationClass.getMethods()) {
            if (isBeanMethod(method)) {
                beanDefinitions.add(makeBeanDefinition(method));
            }
        }
        return beanDefinitions;
    }

    @Override
    public SimpleBeanDefinition makeBeanDefinition(Method method) {
        Bean bean = method.getAnnotation(Bean.class);
        return SimpleBeanDefinition.builder()
                .beanName(beanNamer.makeBeanName(method, bean))
                .scope(scopeResolver.resolveScope(method))
                .beanAssignableClass(method.getReturnType())
                .build();
    }

    private boolean isBeanMethod(Method method) {
        return method.isAnnotationPresent(Bean.class);
    }
}
