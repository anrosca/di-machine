package com.dimachine.core;

import com.dimachine.core.annotation.Bean;
import com.dimachine.core.annotation.Configuration;
import com.dimachine.core.annotation.Scope;
import org.junit.jupiter.api.Test;
import test.FooService;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;

public class AnnotationConfigObjectFactoryTest {

    @Test
    public void shouldBeAbleToInstantiateSingletonBeansFromJavaConfig() {
        DefaultBeanFactory beanFactory = new DefaultBeanFactory(AppConfiguration.class);
        AnnotationConfigObjectFactory configObjectFactory = new AnnotationConfigObjectFactory(beanFactory);

        configObjectFactory.instantiateSingletonsFromConfigClass(new AppConfiguration(), AppConfiguration.class);

        FooService bean = beanFactory.getBean(FooService.class);
        assertNotNull(bean);
    }

    @Test
    public void shouldBeAbleToInstantiatePrototypeBeansFromJavaConfig() {
        DefaultBeanFactory beanFactory = new DefaultBeanFactory(AppConfiguration.class);
        AnnotationConfigObjectFactory configObjectFactory = new AnnotationConfigObjectFactory(beanFactory);

        configObjectFactory.instantiateSingletonsFromConfigClass(new AppConfiguration(), AppConfiguration.class);

        PrototypeService firstPrototype = beanFactory.getBean(PrototypeService.class);
        PrototypeService secondPrototype = beanFactory.getBean(PrototypeService.class);
        assertNotNull(firstPrototype);
        assertNotNull(secondPrototype);
        assertNotSame(firstPrototype, secondPrototype);
    }

    @Configuration
    public static class AppConfiguration {
        @Bean
        public FooService fooService() {
            return new FooService();
        }

        @Scope(BeanScope.PROTOTYPE)
        @Bean
        public PrototypeService prototypeService() {
            return new PrototypeService();
        }
    }

    private static class PrototypeService {
    }
}
