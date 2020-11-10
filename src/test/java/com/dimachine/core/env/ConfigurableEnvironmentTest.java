package com.dimachine.core.env;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class ConfigurableEnvironmentTest {

    private ConfigurableEnvironment environment;

    @BeforeEach
    void setUp() {
        MapPropertySources propertySources = new MapPropertySources(Map.of("application.name", "test"));
        environment = new ConfigurableEnvironment(propertySources);
    }

    @Test
    public void shouldBeAbleToResolvePlaceholders() {
        String resolvedValue = environment.resolvePlaceholder("${application.name}");

        assertEquals("test", resolvedValue);
    }

    @Test
    public void shouldReturnNull_whenPlaceholderCannotBeResolved() {
        assertNull(environment.resolvePlaceholder("${server.port}"));
    }

    @Test
    public void emptyEnvironmentShouldNotContainProperties() {
        environment = new ConfigurableEnvironment();

        assertFalse(environment.containsProperty("application.name"));
    }

    @Test
    public void shouldBeAbleToTellIfEnvironmentContainsProperty() {
        assertTrue(environment.containsProperty("application.name"));
    }

    @Test
    public void shouldBeAbleToGetExistingProperty() {
        assertEquals("test", environment.getProperty("application.name"));
    }

    @Test
    public void shouldBeAbleToGetAllPropertyNames() {
        MapPropertySources propertySources = new MapPropertySources(Map.of(
                "server.port", "8080",
                "application.name", "di-machine"
        ));
        environment = new ConfigurableEnvironment(propertySources);

        assertEquals(Set.of("application.name", "server.port"), environment.getPropertyNames());
    }

    @Test
    public void shouldBeAbleToMergeEnvironments() {
        Environment first = new ConfigurableEnvironment(new MapPropertySources(Map.of("server.port", "8080")));
        Environment second = new ConfigurableEnvironment(new MapPropertySources(Map.of("application.name", "di-machine")));
        Environment mergedEnvironment = new ConfigurableEnvironment();

        mergedEnvironment.merge(first);
        mergedEnvironment.merge(second);

        assertEquals(Set.of("application.name", "server.port"), mergedEnvironment.getPropertyNames());
        assertEquals("8080", mergedEnvironment.getProperty("server.port"));
        assertEquals("di-machine", mergedEnvironment.getProperty("application.name"));
    }
}
