package com.dimachine.core.integration;

import com.dimachine.core.BeanFactory;
import com.dimachine.core.DefaultBeanFactory;
import com.dimachine.core.annotation.Bean;
import com.dimachine.core.annotation.Configuration;
import org.junit.jupiter.api.Test;
import test.FooService;
import test.TestBean;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class NestedConfigurationClassesIT {

    @Test
    public void shouldBeAbleToDefineBeansInNestedConfigurationClasses() {
        BeanFactory beanFactory = new DefaultBeanFactory(AppConfig.class);
        beanFactory.refresh();

        TestBean testBean = beanFactory.getBean(TestBean.class);

        assertNotNull(testBean);
    }

    @Configuration
    public static class AppConfig {

        @Bean
        public FooService fooService() {
            return new FooService();
        }

        @Configuration
        public static class NestedConfig {

            @Bean
            public TestBean testBean(FooService fooService) {
                return new TestBean(fooService);
            }
        }
    }
}
