package com.dimachine.core;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class ClasspathScanner {
    private final String[] packagesToScan;
    private final List<Class<?>> targetAnnotations;

    public ClasspathScanner(List<Class<?>> targetAnnotations, String... packagesToScan) {
        this.targetAnnotations = targetAnnotations;
        this.packagesToScan = packagesToScan;
    }

    public List<String> scan(List<String> additionalPackages) {
        String[] targetPackages = mergePackagesToScan(Arrays.asList(packagesToScan), additionalPackages);
        if (targetPackages.length > 0) {
            return doScanClasspath(targetPackages);
        }
        return List.of();
    }

    private String[] mergePackagesToScan(List<String> basePackages, List<String> additionalPackages) {
        return Stream.concat(basePackages.stream(), additionalPackages.stream())
                .toArray(String[]::new);
    }

    private List<String> doScanClasspath(String[] targetPackages) {
        List<String> foundComponentClasses = new ArrayList<>();
        try (ScanResult scanResult = new ClassGraph().enableAllInfo().acceptPackages(targetPackages).scan()) {
            for (Class<?> scannedAnnotation : targetAnnotations) {
                String routeAnnotation = scannedAnnotation.getName();
                for (ClassInfo classInfo : scanResult.getClassesWithAnnotation(routeAnnotation)) {
                    foundComponentClasses.add(classInfo.getName());
                }
            }
        }
        return foundComponentClasses;
    }
}
