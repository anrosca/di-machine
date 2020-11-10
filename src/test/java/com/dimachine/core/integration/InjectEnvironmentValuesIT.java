package com.dimachine.core.integration;

import com.dimachine.core.BeanFactory;
import com.dimachine.core.DefaultBeanFactory;
import com.dimachine.core.annotation.PropertySource;
import com.dimachine.core.annotation.Value;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Disabled
public class InjectEnvironmentValuesIT {

    @Test
    public void shouldBeAbleToReadEnvironmentProperties() {
        BeanFactory beanFactory = new DefaultBeanFactory(AppConfig.class);
        beanFactory.refresh();

        AppConfig appConfig = beanFactory.getBean(AppConfig.class);

        assertEquals("application.name", appConfig.applicationName);
    }

    @PropertySource("classpath:application.properties")
    public static class AppConfig {
        @Value("${application.name}")
        private String applicationName;
    }
}
