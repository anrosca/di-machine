package com.dimachine.core.locator;

import com.dimachine.core.type.ClassMetadata;

public class AssignableTypeComponentFilter implements ComponentFilter {
    private final Class<?>[] targetClasses;

    public AssignableTypeComponentFilter(Class<?>[] targetClasses) {
        this.targetClasses = targetClasses;
    }

    @Override
    public boolean matches(ClassMetadata classMetadata) {
        for (Class<?> clazz : targetClasses) {
            if (classMetadata.isSubclassOf(clazz.getName())) {
                return true;
            }
        }
        return false;
    }
}
