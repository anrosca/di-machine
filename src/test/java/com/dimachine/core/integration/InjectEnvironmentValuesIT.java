package com.dimachine.core.integration;

import com.dimachine.core.BeanFactory;
import com.dimachine.core.DefaultBeanFactory;
import com.dimachine.core.annotation.Configuration;
import com.dimachine.core.annotation.PropertySource;
import com.dimachine.core.annotation.Value;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InjectEnvironmentValuesIT {

    @Test
    public void shouldBeAbleToReadEnvironmentProperties() {
        BeanFactory beanFactory = new DefaultBeanFactory(AppConfig.class);
        beanFactory.refresh();

        AppConfig appConfig = beanFactory.getBean(AppConfig.class);

        assertEquals("di-machine", appConfig.applicationName);
        assertEquals("di-machine", appConfig.name);
    }

    @Test
    public void whenValueCannotBeResolved_shouldInjectThePlaceholder() {
        BeanFactory beanFactory = new DefaultBeanFactory(NoPropertySourceAppConfig.class);
        beanFactory.refresh();

        NoPropertySourceAppConfig appConfig = beanFactory.getBean(NoPropertySourceAppConfig.class);

        assertEquals("${application.name}", appConfig.applicationName);
    }

    @Configuration
    @PropertySource("classpath:application.properties")
    public static class AppConfig {
        @Value("${application.name}")
        private String applicationName;
        private String name;

        @Value("${application.name}")
        public void setName(String name) {
            this.name = name;
        }
    }

    @Configuration
    public static class NoPropertySourceAppConfig {
        @Value("${application.name}")
        private String applicationName;
    }
}
