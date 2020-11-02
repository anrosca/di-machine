package com.dimachine.core.locator;

import com.dimachine.core.type.ClassMetadata;

public class RejectingComponentFilter implements ComponentFilter {
    @Override
    public boolean matches(ClassMetadata classMetadata) {
        return false;
    }
}
