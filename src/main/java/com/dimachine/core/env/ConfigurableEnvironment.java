package com.dimachine.core.env;

import java.util.LinkedHashMap;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConfigurableEnvironment implements Environment {
    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\$\\{([a-zA-z.0-9]+)}");

    private final PropertySources environmentProperties;

    public ConfigurableEnvironment(PropertySources propertySources) {
        this.environmentProperties = propertySources;
    }

    public ConfigurableEnvironment() {
        this.environmentProperties = new MapPropertySources(new LinkedHashMap<>());
    }

    @Override
    public boolean containsProperty(String name) {
        return environmentProperties.containsProperty(name);
    }

    @Override
    public String getProperty(String name) {
        return environmentProperties.getProperty(name);
    }

    @Override
    public void merge(PropertySources propertySources) {
        this.environmentProperties.merge(propertySources);
    }

    @Override
    public Set<String> getPropertyNames() {
        return environmentProperties.getPropertyNames();
    }

    @Override
    public String resolvePlaceholder(String placeholder) {
        Matcher matcher = PLACEHOLDER_PATTERN.matcher(placeholder);
        if (matcher.find()) {
            String propertyName = matcher.group(1);
            return getProperty(propertyName);
        }
        return null;
    }
}
