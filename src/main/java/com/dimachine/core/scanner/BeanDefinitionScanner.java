package com.dimachine.core.scanner;

import com.dimachine.core.BeanDefinition;
import com.dimachine.core.SimpleBeanDefinition;

import java.lang.reflect.Method;
import java.util.List;

public interface BeanDefinitionScanner {
    List<BeanDefinition> scanBeanDefinitionsFrom(Class<?> configurationClass);

    SimpleBeanDefinition makeBeanDefinition(Method method);
}
