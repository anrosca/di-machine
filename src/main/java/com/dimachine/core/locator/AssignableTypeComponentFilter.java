package com.dimachine.core.locator;

import com.dimachine.core.type.ClassMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AssignableTypeComponentFilter implements ComponentFilter {
    private static final Logger log = LoggerFactory.getLogger(AssignableTypeComponentFilter.class);

    private final Class<?>[] targetClasses;

    public AssignableTypeComponentFilter(Class<?>[] targetClasses) {
        this.targetClasses = targetClasses;
    }

    @Override
    public boolean matches(ClassMetadata classMetadata) {
        for (Class<?> clazz : targetClasses) {
            if (classMetadata.isSubclassOf(clazz.getName())) {
                log.debug("Match found. Class: " + classMetadata.getClassName() + " is assignable from " + clazz.getName());
                return true;
            }
        }
        return false;
    }
}
