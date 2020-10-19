package com.dimachine.core;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;

import java.util.ArrayList;
import java.util.List;

public class ClasspathScanner {
    private final String[] packagesToScan;
    private final List<Class<?>> targetAnnotations;

    public ClasspathScanner(List<Class<?>> targetAnnotations, String... packagesToScan) {
        this.targetAnnotations = targetAnnotations;
        this.packagesToScan = packagesToScan;
    }

    public List<String> scan() {
        List<String> foundBeanDefinitions = new ArrayList<>();
        try (ScanResult scanResult = new ClassGraph().enableAllInfo().acceptPackages(packagesToScan).scan()) {
            for (Class<?> scannedAnnotation : targetAnnotations) {
                String routeAnnotation = scannedAnnotation.getName();
                for (ClassInfo classInfo : scanResult.getClassesWithAnnotation(routeAnnotation)) {
                    foundBeanDefinitions.add(classInfo.getName());
                }
            }
        }
        return foundBeanDefinitions;
    }
}
