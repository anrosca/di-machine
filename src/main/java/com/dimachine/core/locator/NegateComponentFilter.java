package com.dimachine.core.locator;

import com.dimachine.core.type.ClassMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NegateComponentFilter implements ComponentFilter {
    private static final Logger log = LoggerFactory.getLogger(NegateComponentFilter.class);

    private final ComponentFilter componentFilter;

    public NegateComponentFilter(ComponentFilter componentFilter) {
        this.componentFilter = componentFilter;
    }

    @Override
    public boolean matches(ClassMetadata classMetadata) {
        return !componentFilter.matches(classMetadata);
    }
}
