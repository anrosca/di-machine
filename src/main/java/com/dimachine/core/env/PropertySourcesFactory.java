package com.dimachine.core.env;

import com.dimachine.core.annotation.PropertySource;

public class PropertySourcesFactory {
    private final PropertySourceReader propertySourceReader = new PropertySourceReader();

    public PropertySources load(Class<?> configClass) {
        if (configClass.isAnnotationPresent(PropertySource.class)) {
            PropertySource propertySource = configClass.getAnnotation(PropertySource.class);
            return propertySourceReader.read(propertySource);
        }
        return new MapPropertySources();
    }
}
