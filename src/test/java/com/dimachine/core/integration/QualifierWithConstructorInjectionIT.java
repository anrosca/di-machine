package com.dimachine.core.integration;

import com.dimachine.core.BeanFactory;
import com.dimachine.core.DefaultBeanFactory;
import com.dimachine.core.annotation.*;
import org.junit.jupiter.api.Test;
import test.FooService;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class QualifierWithConstructorInjectionIT {

    @Test
    public void shouldBeAbleToQualifyByNameBeanToBeInjected() {
        BeanFactory beanFactory = new DefaultBeanFactory(AppConfig.class);
        beanFactory.refresh();

        AppConfig.NestedConfig bean = beanFactory.getBean(AppConfig.NestedConfig.class);

        assertNotNull(bean.fooService);
    }

    @Configuration
    public static class AppConfig {
        @Bean
        public FooService firstService() {
            return new FooService();
        }

        @Bean
        public FooService secondService() {
            return new FooService();
        }

        @Configuration
        public static class NestedConfig {
            private final FooService fooService;

            public NestedConfig(@Qualifier("secondService") FooService fooService) {
                this.fooService = Objects.requireNonNull(fooService);
            }
        }
    }
}
