package com.dimachine.core.io;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DefaultResourceLoaderTest {

    private final DefaultResourceLoader resourceLoader = new DefaultResourceLoader();

    @Test
    public void shouldBeAbleToLoadClasspathResources() {
        Resource resource = resourceLoader.getResource("classpath:application.properties");

        assertEquals(ClassPathResource.class, resource.getClass());
        assertTrue(resource.exists());
    }

    @Test
    public void whenLoadingResourcesFromUnknownLocations_shouldReturnNull() {
        assertNull(resourceLoader.getResource("ssh:application.properties"));
    }
}
