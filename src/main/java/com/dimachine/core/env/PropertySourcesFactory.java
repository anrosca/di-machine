package com.dimachine.core.env;

import com.dimachine.core.annotation.PropertySource;

public class PropertySourcesFactory {
    private final PropertySourceReader propertySourceReader = new PropertySourceReader();

    public PropertySources load(Class<?> configClass) {
        if (hasPropertySource(configClass)) {
            return readRepeatablePropertySources(configClass);
        }
        return new MapPropertySources();
    }

    private boolean hasPropertySource(Class<?> configClass) {
        return configClass.isAnnotationPresent(PropertySource.class) ||
                configClass.isAnnotationPresent(com.dimachine.core.annotation.PropertySources.class);
    }

    private MapPropertySources readRepeatablePropertySources(Class<?> configClass) {
        PropertySource[] propertySources = configClass.getAnnotationsByType(PropertySource.class);
        MapPropertySources properties = new MapPropertySources();
        for (PropertySource propertySource : propertySources) {
            properties.merge(propertySourceReader.read(propertySource));
        }
        return properties;
    }
}
