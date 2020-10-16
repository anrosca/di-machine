package com.dimachine.core;

import com.dimachine.core.annotation.Component;
import io.github.classgraph.AnnotationInfo;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;

import java.util.HashSet;
import java.util.Set;

public class ClasspathScanner {
    private final String[] packagesToScan;

    public ClasspathScanner(String[] packagesToScan) {
        this.packagesToScan = packagesToScan;
    }

    public Set<BeanDefinition> scan() {
        Set<BeanDefinition> foundBeanDefinitions = new HashSet<>();
        String routeAnnotation = Component.class.getName();
        try (ScanResult scanResult =
                     new ClassGraph()
                             .enableAllInfo()
                             .acceptPackages(packagesToScan)
                             .scan()) {
            for (ClassInfo routeClassInfo : scanResult.getClassesWithAnnotation(routeAnnotation)) {
                AnnotationInfo routeAnnotationInfo = routeClassInfo.getAnnotationInfo(routeAnnotation);
                String className = routeClassInfo.getName();
                BeanDefinition beanDefinition = new SimpleBeanDefinition(className, makeBeanName(className));
                foundBeanDefinitions.add(beanDefinition);
            }
        }
        return foundBeanDefinitions;
    }

    private String makeBeanName(String className) {
        return className.substring(className.lastIndexOf(".") + 1);
    }
}
