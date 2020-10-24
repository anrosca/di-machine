package com.dimachine.core.locator;

import com.dimachine.core.annotation.ComponentScan;
import com.dimachine.core.annotation.FilterType;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ComponentTraitsFactory {
    private static final Map<FilterType, Function<Class<?>[], ComponentFilter>> componentFilters = new EnumMap<>(FilterType.class);

    static {
        componentFilters.put(FilterType.ANNOTATION, AnnotationComponentFilter::new);
        componentFilters.put(FilterType.ASSIGNABLE_TYPE, AssignableTypeComponentFilter::new);
    }

    public ComponentTraits from(ComponentScan componentScan) {
        ComponentFilter filter = makeComponentFilter(componentScan);
        List<String> componentPackages = List.copyOf(getPackagesToScanFrom(componentScan));
        return new ComponentTraits(componentPackages, filter);
    }

    private ComponentFilter makeComponentFilter(ComponentScan componentScan) {
        ComponentScan.Filter[] filters = componentScan.includeFilters();
        if (filters.length > 0) {
            return new OrComponentFilterCombiner(makeComponentFilters(filters));
        }
        return new NoOpComponentFilter();
    }

    private List<ComponentFilter> makeComponentFilters(ComponentScan.Filter[] filters) {
        List<ComponentFilter> componentFilters = new ArrayList<>();
        for (ComponentScan.Filter filter : filters) {
            componentFilters.add(makeFilterFrom(filter));
        }
        return componentFilters;
    }

    private ComponentFilter makeFilterFrom(ComponentScan.Filter filter) {
        return componentFilters.getOrDefault(filter.type(), (classes) -> new NoOpComponentFilter())
                .apply(filter.classes());
    }

    private Set<String> getPackagesToScanFrom(ComponentScan componentScan) {
        Set<String> packagesToScan = new HashSet<>();
        packagesToScan.addAll(Arrays.asList(componentScan.basePackages()));
        packagesToScan.addAll(Arrays.asList(componentScan.value()));
        packagesToScan.addAll(getTypeSafeBasePackages(componentScan.basePackageClasses()));
        return packagesToScan;
    }

    private List<String> getTypeSafeBasePackages(Class<?>[] basePackageClasses) {
        return Arrays.stream(basePackageClasses)
                .map(Class::getPackageName)
                .collect(Collectors.toList());
    }
}
