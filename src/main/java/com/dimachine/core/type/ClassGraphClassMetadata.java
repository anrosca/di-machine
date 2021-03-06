package com.dimachine.core.type;

import io.github.classgraph.ClassInfo;

import java.util.List;
import java.util.stream.Collectors;

public class ClassGraphClassMetadata implements ClassMetadata {
    private final ClassInfo classInfo;

    public ClassGraphClassMetadata(ClassInfo classInfo) {
        this.classInfo = classInfo;
    }

    @Override
    public String getClassName() {
        return classInfo.getName();
    }

    @Override
    public boolean isAbstract() {
        return classInfo.isAbstract();
    }

    @Override
    public boolean isInterface() {
        return classInfo.isInterface();
    }

    @Override
    public boolean isFinal() {
        return classInfo.isFinal();
    }

    @Override
    public List<AnnotationMetadata> getAnnotations() {
        return classInfo.getAnnotations()
                .stream()
                .map(ClassGraphAnnotationMetadata::new)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isSubclassOf(String className) {
        if (className.equals(classInfo.getName()))
            return true;
        boolean isSubclass = classInfo.implementsInterface(className);
        ClassInfo superclass = classInfo;
        while ((superclass = superclass.getSuperclass()) != null) {
            isSubclass |= superclass.getName().equals(className);
        }
        return isSubclass;
    }

    @Override
    public String toString() {
        return classInfo.toString();
    }
}
