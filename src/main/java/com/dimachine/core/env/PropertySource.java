package com.dimachine.core.env;

public interface PropertySource {

    boolean containsProperty(String name);

    String getProperty(String name);
}
