package com.dimachine.core.integration;

import com.dimachine.core.BeanFactory;
import com.dimachine.core.DefaultBeanFactory;
import com.dimachine.core.annotation.Bean;
import com.dimachine.core.annotation.Configuration;
import com.dimachine.core.annotation.Import;
import org.junit.jupiter.api.Test;
import test.FooService;
import test.TestBean;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ImportConfigClassesIT {

    @Test
    public void shouldBeAbleToImportConfigurationClasses() {
        BeanFactory beanFactory = new DefaultBeanFactory(ImportAppConfig.class);
        beanFactory.refresh();

        TestBean bean = beanFactory.getBean(TestBean.class);

        assertNotNull(bean);
    }

    @Configuration
    public static class AppConfig {

        @Bean
        public FooService fooService() {
            return new FooService();
        }
    }

    @Configuration
    public static class YetAnotherConfig {
        @Bean
        public TestBean testBean(FooService fooService) {
            return new TestBean(fooService);
        }
    }

    @Import({AppConfig.class, YetAnotherConfig.class})
    @Configuration
    public static class ImportAppConfig {
    }
}
