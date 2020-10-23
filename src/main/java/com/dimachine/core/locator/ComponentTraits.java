package com.dimachine.core.locator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ComponentTraits {
    private final List<String> componentPackages;
    private final ComponentFilter componentFilter;

    public ComponentTraits(List<String> componentPackages, ComponentFilter componentFilter) {
        this.componentPackages = new ArrayList<>(componentPackages);
        this.componentFilter = componentFilter;
    }

    public ComponentTraits(List<String> componentPackages) {
        this.componentPackages = new ArrayList<>(componentPackages);
        this.componentFilter = new NoOpComponentFilter();
    }

    public List<String> getComponentPackages() {
        return Collections.unmodifiableList(componentPackages);
    }

    public ComponentFilter getComponentFilter() {
        return componentFilter;
    }

    public void addTrait(ComponentTraits newTraits) {
        componentPackages.addAll(newTraits.getComponentPackages());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ComponentTraits that = (ComponentTraits) o;

        if (!componentPackages.equals(that.componentPackages)) return false;
        return componentFilter.equals(that.componentFilter);
    }

    @Override
    public int hashCode() {
        int result = componentPackages.hashCode();
        result = 31 * result + componentFilter.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "ComponentTraits{" +
                "componentPackages=" + componentPackages +
                ", componentFilter=" + componentFilter +
                '}';
    }
}
