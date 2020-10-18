package com.dimachine.core;

import com.dimachine.core.annotation.Component;
import com.dimachine.core.annotation.Service;
import io.github.classgraph.AnnotationInfo;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;

import java.util.ArrayList;
import java.util.List;

public class ClasspathScanner {
    private final String[] packagesToScan;
    private final BeanNamer beanNamer = new DefaultBeanNamer();
    private final List<Class<?>> targetAnnotations = List.of(Component.class, Service.class);

    public ClasspathScanner(String...packagesToScan) {
        this.packagesToScan = packagesToScan;
    }

    public List<BeanDefinition> scan() {
        List<BeanDefinition> foundBeanDefinitions = new ArrayList<>();
        try (ScanResult scanResult = new ClassGraph().enableAllInfo().acceptPackages(packagesToScan).scan()) {
            for (Class<?> scannedAnnotation : targetAnnotations) {
                String routeAnnotation = scannedAnnotation.getName();
                for (ClassInfo routeClassInfo : scanResult.getClassesWithAnnotation(routeAnnotation)) {
                    BeanDefinition beanDefinition = makeBeanDefinition(routeAnnotation, routeClassInfo);
                    foundBeanDefinitions.add(beanDefinition);
                }
            }
        }
        return foundBeanDefinitions;
    }

    private BeanDefinition makeBeanDefinition(String routeAnnotation, ClassInfo routeClassInfo) {
        AnnotationInfo routeAnnotationInfo = routeClassInfo.getAnnotationInfo(routeAnnotation);
        String explicitBeanName = (String) routeAnnotationInfo.getParameterValues().getValue("value");
        String className = routeClassInfo.getName();
        return new SimpleBeanDefinition(className, makeBeanName(className, explicitBeanName));
    }

    private String makeBeanName(String className, String explicitBeanName) {
        return explicitBeanName.isEmpty() ? beanNamer.makeBeanName(className) : explicitBeanName;
    }
}
