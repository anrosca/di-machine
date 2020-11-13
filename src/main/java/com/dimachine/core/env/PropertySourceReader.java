package com.dimachine.core.env;

import com.dimachine.core.annotation.PropertySource;
import com.dimachine.core.io.DefaultResourceLoader;
import com.dimachine.core.io.Resource;
import com.dimachine.core.io.ResourceLoader;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

public class PropertySourceReader {
    private final ResourceLoader resourceLoader = new DefaultResourceLoader();

    public PropertySources read(PropertySource propertySource) {
        Resource resource = resourceLoader.getResource(propertySource.value());
        if (!propertySource.ignoreIfNotFound() && !resource.exists()) {
            throw new ResourceNotFoundException("Resource " + propertySource.value() +
                    " was not found and @PropertySource.ignoreIfNotFound is false");
        }
        if (resource.exists()) {
            return makePropertySourceFrom(resource);
        }
        return new MapPropertySources();
    }

    private PropertySources makePropertySourceFrom(Resource resource) {
        try (InputStream inputStream = resource.getInputStream()) {
            Properties properties = new Properties();
            properties.load(inputStream);
            return new MapPropertySources(toMap(properties));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Map<String, String> toMap(Properties properties) {
        Map<String, String> propertyMap = new LinkedHashMap<>();
        properties.forEach((key, value) -> propertyMap.put((String) key, (String) value));
        return propertyMap;
    }
}
