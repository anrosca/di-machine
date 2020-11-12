package com.dimachine.core.integration;

import com.dimachine.core.BeanFactory;
import com.dimachine.core.DefaultBeanFactory;
import com.dimachine.core.DuplicateBeanNameException;
import com.dimachine.core.annotation.Bean;
import com.dimachine.core.annotation.Configuration;
import org.junit.jupiter.api.Test;
import test.FooService;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class DuplicateBeanNamesIT {

    @Test
    public void shouldThrowDuplicateBeanNameException_whenThereAreNonUniqueBeanNames() {
        BeanFactory beanFactory = new DefaultBeanFactory(AppConfig.class);

        assertThrows(DuplicateBeanNameException.class, beanFactory::refresh);
    }

    @Configuration
    public static class AppConfig {

        @Bean
        public FooService fooService() {
            return new FooService();
        }

        @Bean(name = "fooService")
        public FooService yetAnotherService() {
            return new FooService();
        }
    }
}
