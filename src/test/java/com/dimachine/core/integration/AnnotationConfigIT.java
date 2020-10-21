package com.dimachine.core.integration;

import com.dimachine.core.DefaultBeanFactory;
import com.dimachine.core.annotation.Bean;
import com.dimachine.core.annotation.Configuration;
import org.junit.jupiter.api.Test;
import test.FooService;
import test.TestBean;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AnnotationConfigIT {

    @Test
    public void shouldBeAbleToGetBeansViaJavaConfiguration() throws Exception {
        TestBean bean;
        try (DefaultBeanFactory beanFactory = new DefaultBeanFactory(AppConfiguration.class)) {
            beanFactory.refresh();

            bean = beanFactory.getBean(TestBean.class);
            assertTrue(bean.initMethodWasCalled());
            assertNotNull(bean.getAutowiredField());
            assertNotNull(bean.getAutowireList());
            assertNotNull(bean.getAutowireMap());
        }
        assertTrue(bean.destroyMethodWasCalled());
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
