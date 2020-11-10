package com.dimachine.core.env;

import java.util.Set;

public interface PropertySources {

    boolean containsProperty(String name);

    String getProperty(String name);

    void merge(PropertySources propertySources);

    Set<String> getPropertyNames();
}
