package com.dimachine.core.env;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class MapPropertySources implements PropertySources {
    private final Map<String, String> source;

    public MapPropertySources(Map<String, String> source) {
        this.source = source;
    }

    public MapPropertySources() {
        this.source = new LinkedHashMap<>();
    }

    @Override
    public boolean containsProperty(String name) {
        return source.containsKey(name);
    }

    @Override
    public String getProperty(String name) {
        return source.get(name);
    }

    @Override
    public void merge(PropertySources propertySources) {
        for (String propertyName : propertySources.getPropertyNames()) {
            source.put(propertyName, propertySources.getProperty(propertyName));
        }
    }

    @Override
    public Set<String> getPropertyNames() {
        return source.keySet();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MapPropertySources that = (MapPropertySources) o;

        return source.equals(that.source);
    }

    @Override
    public int hashCode() {
        return source.hashCode();
    }

    @Override
    public String toString() {
        return source.toString();
    }
}
