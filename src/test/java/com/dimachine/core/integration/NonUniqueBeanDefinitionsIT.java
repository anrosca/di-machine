package com.dimachine.core.integration;

import com.dimachine.core.BeanFactory;
import com.dimachine.core.DefaultBeanFactory;
import com.dimachine.core.NonUniqueBeanDefinitionException;
import com.dimachine.core.annotation.Bean;
import com.dimachine.core.annotation.Configuration;
import org.junit.jupiter.api.Test;
import test.FooService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class NonUniqueBeanDefinitionsIT {

    @Test
    public void shouldThrowNonUniqueBeanDefinitionException_whenThereAreMoreThanOneBeanWithTheSameType() {
        BeanFactory beanFactory = new DefaultBeanFactory(AppConfig.class);
        beanFactory.refresh();

        Exception exception = assertThrows(NonUniqueBeanDefinitionException.class, () -> beanFactory.getBean(FooService.class));
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
    }
}
