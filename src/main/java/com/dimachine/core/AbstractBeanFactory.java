package com.dimachine.core;

import com.dimachine.core.annotation.Configuration;
import com.dimachine.core.annotation.Import;
import com.dimachine.core.env.*;

import java.util.Arrays;

public abstract class AbstractBeanFactory extends AbstractBeanDefinitionRegistry implements BeanFactory, BeanDefinitionRegistry {
    protected final Environment environment = new ConfigurableEnvironment();

    protected void makeEnvironment() {
        environment.merge(new MapPropertySources(System.getenv()));
        DefaultBeanDefinitionMaker beanDefinitionMaker = new DefaultBeanDefinitionMaker();
        String environmentClassName = environment.getClass().getName();
        registerSingleton(beanDefinitionMaker.makeBeanDefinition(environmentClassName), environment);
        enrichEnvironment();
    }

    private void enrichEnvironment() {
        for (BeanDefinition beanDefinition : beanDefinitions) {
            Class<?> beanClass = beanDefinition.getRealBeanClass();
            readConfigClass(beanClass);
            Arrays.stream(beanClass.getDeclaredClasses())
                        .forEach(this::readConfigClass);
        }
    }

    private void readConfigClass(Class<?> beanClass) {
        if (beanClass.isAnnotationPresent(Configuration.class)) {
            processPropertySources(beanClass);
            if (beanClass.isAnnotationPresent(Import.class)) {
                Import theImport = beanClass.getAnnotation(Import.class);
                Arrays.stream(theImport.value()).forEach(this::readConfigClass);
            }
        }
    }

    private void processPropertySources(Class<?> configClass) {
        PropertySourcesFactory processor = new PropertySourcesFactory();
        PropertySources propertySources = processor.load(configClass);
        environment.merge(propertySources);
    }
}
