package com.dimachine.core.io;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;

public class ClassPathResourceTest {

    @Test
    public void shouldBeAbleToOpenResourceStream() throws IOException {
        ClassPathResource resource = new ClassPathResource("application.properties");

        try (InputStream inputStream = resource.getInputStream()) {
            assertNotNull(inputStream);
        }
    }

    @Test
    public void shouldBeAbleToTellIfResourceExists() {
        ClassPathResource resource = new ClassPathResource("application.properties");

        assertTrue(resource.exists());
    }

    @Test
    public void shouldBeAbleToTellIfGivenResourceDoesNotExist() {
        ClassPathResource resource = new ClassPathResource("funky_properties.properties");

        assertFalse(resource.exists());
    }

    @Test
    public void shouldBeAbleToGetResourceURL() {
        ClassPathResource resource = new ClassPathResource("application.properties");

        URL url = resource.getURL();

        assertTrue(url.toString().endsWith("application.properties"));
    }
}
