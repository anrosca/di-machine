package com.dimachine.core.locator;

import com.dimachine.core.type.ClassMetadata;
import com.dimachine.core.util.ReflectionUtils;

import java.util.ArrayList;
import java.util.List;

public class CustomComponentFilter implements ComponentFilter {
    private final List<TypeFilter> typeFilters = new ArrayList<>();

    public CustomComponentFilter(Class<?>[] classes) {
        for (Class<?> filterClass : classes) {
            checkFilterType(filterClass);
            typeFilters.add((TypeFilter) ReflectionUtils.makeInstance(filterClass));
        }
    }

    private void checkFilterType(Class<?> filterClass) {
        if (!TypeFilter.class.isAssignableFrom(filterClass)) {
            throw new IncorrectTypeFilterClassException(filterClass + " is expected to implement " +
                    TypeFilter.class.getName() + " interface.");
        }
    }

    @Override
    public boolean matches(ClassMetadata classMetadata) {
        if (typeFilters.isEmpty())
            return true;
        return doEvaluateMatch(classMetadata);
    }

    private boolean doEvaluateMatch(ClassMetadata classMetadata) {
        for (TypeFilter typeFilter : typeFilters) {
            if (typeFilter.match(() -> classMetadata)) {
                return true;
            }
        }
        return false;
    }
}
