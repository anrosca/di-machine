package com.dimachine.core.env;

import java.util.Map;

public class MapPropertySource implements PropertySource {
    private final Map<String, String> source;

    public MapPropertySource(Map<String, String> source) {
        this.source = source;
    }

    @Override
    public boolean containsProperty(String name) {
        return source.containsKey(name);
    }

    @Override
    public String getProperty(String name) {
        return source.get(name);
    }
}
