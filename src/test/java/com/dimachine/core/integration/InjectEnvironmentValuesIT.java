package com.dimachine.core.integration;

import com.dimachine.core.BeanFactory;
import com.dimachine.core.DefaultBeanFactory;
import com.dimachine.core.annotation.*;
import com.dimachine.core.env.Environment;
import com.dimachine.core.env.ResourceNotFoundException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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
    public void shouldBeAbleToReadEnvironmentPropertiesAsConstructorParameters() {
        BeanFactory beanFactory = new DefaultBeanFactory(ConstructorInjectedValuesConfig.class);
        beanFactory.refresh();

        ConstructorInjectedValuesConfig appConfig = beanFactory.getBean(ConstructorInjectedValuesConfig.class);

        assertEquals("di-machine", appConfig.applicationName);
    }

    @Test
    public void whenValueCannotBeResolved_shouldInjectThePlaceholder() {
        BeanFactory beanFactory = new DefaultBeanFactory(NoPropertySourceAppConfig.class);
        beanFactory.refresh();

        NoPropertySourceAppConfig appConfig = beanFactory.getBean(NoPropertySourceAppConfig.class);

        assertEquals("${application.name}", appConfig.applicationName);
    }

    @Test
    public void whenPropertyCannotBeResolvedAndPlaceholderHasDefaultValue_shouldInjectTheDefaultValue() {
        BeanFactory beanFactory = new DefaultBeanFactory(DefaultValueAppConfig.class);
        beanFactory.refresh();

        DefaultValueAppConfig appConfig = beanFactory.getBean(DefaultValueAppConfig.class);

        assertEquals("8080", appConfig.serverPort);
    }

    @Test
    public void shouldBeAbleToSpecifyMultiplePropertySources() {
        BeanFactory beanFactory = new DefaultBeanFactory(MultiplePropertySourcesConfig.class);
        beanFactory.refresh();

        MultiplePropertySourcesConfig config = beanFactory.getBean(MultiplePropertySourcesConfig.class);

        assertEquals("di-machine", config.applicationName);
        assertEquals("8080", config.serverPort);
    }

    @Test
    public void shouldThrowResourceNotFoundException_whenResourceIsMissingAndIgnoreIfNotFoundIsFalse() {
        BeanFactory beanFactory = new DefaultBeanFactory(MissingPropertySourcesConfig.class);
        assertThrows(ResourceNotFoundException.class, beanFactory::refresh);
    }

    @Test
    public void shouldBeAbleToIgnoreMissingResources_whenResourceIsMissingAndIgnoreIfNotFoundIsSetToTrue() {
        BeanFactory beanFactory = new DefaultBeanFactory(IgnoreMissingPropertySourcesConfig.class);
        assertDoesNotThrow(beanFactory::refresh);
    }

    @Test
    public void shouldBeAbleToReadPropertySourcesFromNestedConfigurationClasses() {
        BeanFactory beanFactory = new DefaultBeanFactory(NestedConfigProperties.class);
        beanFactory.refresh();

        Environment environment = beanFactory.getBean(Environment.class);

        assertEquals("di-machine", environment.getProperty("application.name"));
    }

    @Test
    public void shouldBeAbleToReadPropertySourcesFromImportedConfigurationClasses() {
        BeanFactory beanFactory = new DefaultBeanFactory(ImportedConfiguration.class);
        beanFactory.refresh();

        Environment environment = beanFactory.getBean(Environment.class);

        assertEquals("di-machine", environment.getProperty("application.name"));
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
    @PropertySource("classpath:application.properties")
    public static class DefaultValueAppConfig {
        @Value("${server.port:8080}")
        private String serverPort;
    }

    @Configuration
    public static class NoPropertySourceAppConfig {
        @Value("${application.name}")
        private String applicationName;
    }

    @Configuration
    @PropertySource("classpath:application.properties")
    public static class ConstructorInjectedValuesConfig {
        private final String applicationName;

        public ConstructorInjectedValuesConfig(@Value("${application.name}") String applicationName) {
            this.applicationName = applicationName;
        }
    }

    @Configuration
    @PropertySources({
            @PropertySource("classpath:application.properties"),
            @PropertySource("classpath:webApp.properties")
    })
    public static class MultiplePropertySourcesConfig {
        private final String applicationName;
        private final String serverPort;

        public MultiplePropertySourcesConfig(@Value("${application.name}") String applicationName,
                                             @Value("${server.port}") String serverPort) {
            this.applicationName = applicationName;
            this.serverPort = serverPort;
        }
    }

    @Configuration
    @PropertySource(value = "classpath:appConfig.properties", ignoreIfNotFound = false)
    public static class MissingPropertySourcesConfig {
    }

    @Configuration
    @PropertySource(value = "classpath:appConfig.properties", ignoreIfNotFound = true)
    public static class IgnoreMissingPropertySourcesConfig {
    }

    @Configuration
    public static class NestedConfigProperties {

        @Configuration
        @PropertySource("classpath:application.properties")
        public static class AppConfig {
        }
    }

    @Import(AppConfig.class)
    @Configuration
    public static class ImportedConfiguration {
    }
}
