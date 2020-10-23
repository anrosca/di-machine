package com.dimachine.core.locator;

import com.dimachine.core.type.ClassMetadata;

public class NoOpComponentFilter implements ComponentFilter {
    @Override
    public boolean matches(ClassMetadata classMetadata) {
        return true;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null)
            return false;
        return getClass().equals(other.getClass());
    }

    @Override
    public int hashCode() {
        return 31;
    }
}
