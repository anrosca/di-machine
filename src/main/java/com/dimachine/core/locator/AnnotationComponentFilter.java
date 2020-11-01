package com.dimachine.core.locator;

import com.dimachine.core.type.AnnotationMetadata;
import com.dimachine.core.type.ClassMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class AnnotationComponentFilter implements ComponentFilter {
    private static final Logger log = LoggerFactory.getLogger(AnnotationComponentFilter.class);

    private final Class<?>[] targetAnnotations;

    public AnnotationComponentFilter(Class<?>... targetAnnotations) {
        if (!isAnnotationsOnly(targetAnnotations)) {
            throw new IllegalArgumentException(Arrays.toString(targetAnnotations) +
                    " was expected to contain only annotation types.");
        }
        this.targetAnnotations = targetAnnotations;
    }

    private boolean isAnnotationsOnly(Class<?>[] targetAnnotations) {
        return Arrays.stream(targetAnnotations)
                .allMatch(Class::isAnnotation);
    }

    @Override
    public boolean matches(ClassMetadata classMetadata) {
        for (Class<?> desiredAnnotationClassName : targetAnnotations) {
            for (AnnotationMetadata annotation : classMetadata.getAnnotations()) {
                if (annotation.getAnnotationClassName().equals(desiredAnnotationClassName.getName())) {
                    log.debug("Match found. Class {} is annotated with {}", classMetadata.getClassName(),
                            desiredAnnotationClassName.getName());
                    return true;
                }
            }
        }
        return false;
    }
}
