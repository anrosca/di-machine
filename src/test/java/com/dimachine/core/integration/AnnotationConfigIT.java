package com.dimachine.core.integration;

import com.dimachine.core.DefaultBeanFactory;
import com.dimachine.core.annotation.Bean;
import com.dimachine.core.annotation.Configuration;
import org.junit.jupiter.api.Test;
import test.FooService;
import test.TestBean;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class AnnotationConfigIT {

    @Test
    public void shouldBeAbleToGetBeansViaJavaConfiguration() {
        DefaultBeanFactory beanFactory = new DefaultBeanFactory(AppConfiguration.class);
        beanFactory.refresh();

        TestBean bean = beanFactory.getBean(TestBean.class);

        assertTrue(bean.initMethodWasCalled());
    }

    @Configuration
    public static class AppConfiguration {

        @Bean
        public TestBean testBean(FooService fooService) {
            return new TestBean(fooService);
        }

        @Bean
        public FooService fooService() {
            System.out.println("yay");
            return new FooService();
        }
    }
}
