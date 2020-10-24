package com.dimachine.core.locator;

import com.dimachine.core.annotation.Component;
import com.dimachine.core.annotation.Service;
import com.dimachine.core.type.AnnotationMetadata;
import com.dimachine.core.type.ClassMetadata;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AnnotationComponentFilterTest {

    @Test
    public void shouldNotAcceptConstructorParametersWithAreNotAnnotationTypes() {
        IllegalArgumentException thrownException =
                assertThrows(IllegalArgumentException.class, () -> new AnnotationComponentFilter(String.class));
        assertEquals("[class java.lang.String] was expected to contain only annotation types.", thrownException.getMessage());
    }

    @Test
    public void shouldMatchClassMetadataAnnotatedWithWantedAnnotation() {
        ClassMetadata classMetadata = makeClassMetadataWithAnnotation(Component.class);
        AnnotationComponentFilter componentFilter = new AnnotationComponentFilter(Component.class);

        assertTrue(componentFilter.matches(classMetadata));
    }

    @Test
    public void shouldNotMatchClassMetadataAnnotatedWithUnrelatedAnnotations() {
        ClassMetadata classMetadata = makeClassMetadataWithAnnotation(Service.class);
        AnnotationComponentFilter componentFilter = new AnnotationComponentFilter(Component.class);

        assertFalse(componentFilter.matches(classMetadata));
    }

    @Test
    public void shouldNotMatchClassMetadataWithoutAnnotations() {
        ClassMetadata classMetadata = mock(ClassMetadata.class);
        AnnotationComponentFilter componentFilter = new AnnotationComponentFilter(Component.class);

        assertFalse(componentFilter.matches(classMetadata));
    }

    private static ClassMetadata makeClassMetadataWithAnnotation(Class<?> annotation) {
        ClassMetadata classMetadata = mock(ClassMetadata.class);
        AnnotationMetadata annotationMetadata = mock(AnnotationMetadata.class);
        when(classMetadata.getAnnotations()).thenReturn(List.of(annotationMetadata));
        when(annotationMetadata.getAnnotationClassName()).thenReturn(annotation.getName());
        return classMetadata;
    }
}
