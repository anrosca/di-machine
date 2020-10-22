package com.dimachine.core.locator;

import com.dimachine.core.annotation.ComponentScan;
import com.dimachine.core.annotation.ComponentScans;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ComponentPackageLocator {

    public List<String> locate(List<? extends Class<?>> classesToScan) {
        Set<String> packagesToScan = new HashSet<>();
        for (Class<?> configClass : classesToScan) {
            ComponentScan[] annotations = readComponentScanAnnotations(configClass);
            for (ComponentScan componentScan : annotations) {
                packagesToScan.addAll(getPackagesToScanFrom(componentScan));
            }
        }
        return List.copyOf(packagesToScan);
    }

    private ComponentScan[] readComponentScanAnnotations(Class<?> configClass) {
        if (configClass.isAnnotationPresent(ComponentScan.class)) {
            return configClass.getAnnotationsByType(ComponentScan.class);
        } else if (configClass.isAnnotationPresent(ComponentScans.class)) {
            return configClass.getAnnotation(ComponentScans.class).value();
        }
        return new ComponentScan[0];
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
