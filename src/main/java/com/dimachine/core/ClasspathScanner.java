package com.dimachine.core;

import com.dimachine.core.type.ClassMetadata;
import com.dimachine.core.type.ClassGraphClassMetadata;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class ClasspathScanner {
    private final String[] packagesToScan;
    private final List<Class<? extends Annotation>> targetAnnotations;

    public ClasspathScanner(List<Class<? extends Annotation>> targetAnnotations, String... packagesToScan) {
        this.targetAnnotations = targetAnnotations;
        this.packagesToScan = packagesToScan;
    }

    public List<ClassMetadata> scan(List<String> additionalPackages) {
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

    private List<ClassMetadata> doScanClasspath(String[] targetPackages) {
        List<ClassMetadata> foundComponentClasses = new ArrayList<>();
        try (ScanResult scanResult = new ClassGraph().enableAllInfo().acceptPackages(targetPackages).scan()) {
            for (Class<?> scannedAnnotation : targetAnnotations) {
                String routeAnnotation = scannedAnnotation.getName();
                for (ClassInfo classInfo : scanResult.getClassesWithAnnotation(routeAnnotation)) {
                    if (!classInfo.isAnnotation()) {
                        foundComponentClasses.add(new ClassGraphClassMetadata(classInfo));
                    }
                }
            }
        }
        return foundComponentClasses;
    }
}
