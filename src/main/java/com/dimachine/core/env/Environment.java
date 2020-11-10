package com.dimachine.core.env;

public interface Environment extends PropertySources {

    String resolvePlaceholder(String placeholder);
}
