package com.dimachine.core.env;

import com.dimachine.core.annotation.Configuration;
import com.dimachine.core.annotation.PropertySource;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class PropertySourcesFactoryTest {

    @Test
    public void whenLoadingConfigurationWithNoPropertySources_shouldReturnEmptyPropertySource() {
        PropertySourcesFactory loader = new PropertySourcesFactory();

        PropertySources loadedProperties = loader.load(NoPropertySourceAppConfig.class);

        assertEquals(new MapPropertySources(), loadedProperties);
    }

    @Test
    public void shouldBeAbleToLoadPropertySourcesFromClasspathFiles() {
        PropertySourcesFactory loader = new PropertySourcesFactory();

        PropertySources loadedProperties = loader.load(AppConfig.class);

        MapPropertySources expectedProperties = new MapPropertySources(Map.of("application.name", "di-machine"));
        assertEquals(expectedProperties, loadedProperties);
    }

    @Configuration
    public static class NoPropertySourceAppConfig {
    }

    @Configuration
    @PropertySource("classpath:application.properties")
    public static class AppConfig {
    }
}
