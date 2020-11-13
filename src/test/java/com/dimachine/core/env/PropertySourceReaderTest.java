package com.dimachine.core.env;

import com.dimachine.core.annotation.PropertySource;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class PropertySourceReaderTest {

    @Test
    public void whenReadingAMissingResourceAndIgnoreIfNotFoundIsTrue_shouldReturnAnEmptyPropertySource() {
        PropertySourceReader reader = new PropertySourceReader();

        PropertySources actualPropertySources = reader.read(IgnoreMissingPropertySource.class.getAnnotation(PropertySource.class));

        assertEquals(new MapPropertySources(), actualPropertySources);
    }

    @Test
    public void whenReadingAMissingResourceAndIgnoreIfNotFoundIsFalse_shouldThrowResourceNotFoundException() {
        PropertySourceReader reader = new PropertySourceReader();

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> reader.read(MissingPropertySource.class.getAnnotation(PropertySource.class)));
        assertEquals("Resource classpath:appConfig.properties was not found and @PropertySource.ignoreIfNotFound is false", exception.getMessage());
    }

    @Test
    public void shouldBeAbleToReadExistingPropertySources() {
        PropertySourceReader reader = new PropertySourceReader();

        PropertySources actualPropertySources = reader.read(RegularPropertySource.class.getAnnotation(PropertySource.class));

        assertEquals(new MapPropertySources(Map.of("application.name", "di-machine")), actualPropertySources);
    }

    @PropertySource(value = "classpath:appConfig.properties", ignoreIfNotFound = true)
    private static class IgnoreMissingPropertySource {
    }

    @PropertySource(value = "classpath:appConfig.properties", ignoreIfNotFound = false)
    private static class MissingPropertySource {
    }

    @PropertySource("classpath:application.properties")
    private static class RegularPropertySource {
    }
}
