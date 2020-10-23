package com.dimachine.core.locator;

import com.dimachine.core.type.ClassMetadata;

import java.util.ArrayList;
import java.util.List;

public class OrComponentFilterCombiner implements ComponentFilter {
    private final List<ComponentFilter> componentFilters;

    public OrComponentFilterCombiner(List<ComponentFilter> componentFilters) {
        this.componentFilters = componentFilters;
    }

    public OrComponentFilterCombiner() {
        this.componentFilters = new ArrayList<>();
    }

    @Override
    public boolean matches(ClassMetadata classMetadata) {
        if (componentFilters.isEmpty())
            return true;
        return doEvaluateMatch(classMetadata);
    }

    private boolean doEvaluateMatch(ClassMetadata classMetadata) {
        boolean matchResult = false;
        for (ComponentFilter filter : componentFilters) {
            matchResult |= filter.matches(classMetadata);
        }
        return matchResult;
    }

    public void combineWith(ComponentFilter componentFilter) {
        componentFilters.add(componentFilter);
    }
}
