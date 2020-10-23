package com.dimachine.core.type;

import io.github.classgraph.ClassInfo;

public class ClassGraphAnnotationMetadata implements AnnotationMetadata {
    private final ClassInfo classInfo;

    public ClassGraphAnnotationMetadata(ClassInfo classInfo) {
        if (!classInfo.isAnnotation()) {
            throw new IllegalArgumentException("Class " + classInfo.getName() + " is not an annotation");
        }
        this.classInfo = classInfo;
    }

    @Override
    public String getAnnotationClassName() {
        return classInfo.getName();
    }
}
