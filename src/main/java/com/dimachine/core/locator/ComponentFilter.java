package com.dimachine.core.locator;

import com.dimachine.core.type.ClassMetadata;

public interface ComponentFilter {

    boolean matches(ClassMetadata classMetadata);

    default ComponentFilter negate() {
        return new NegateComponentFilter(this);
    }
}
