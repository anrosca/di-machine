package com.dimachine.core;

import com.dimachine.core.annotation.Configuration;
import com.dimachine.core.env.*;

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
            if (beanClass.isAnnotationPresent(Configuration.class)) {
                processPropertySources(beanClass);
            }
        }
    }

    private void processPropertySources(Class<?> configClass) {
        PropertySourcesFactory processor = new PropertySourcesFactory();
        PropertySources propertySources = processor.load(configClass);
        environment.merge(propertySources);
    }
}
