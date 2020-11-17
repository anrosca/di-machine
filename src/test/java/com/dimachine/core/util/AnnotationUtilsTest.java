package com.dimachine.core.util;

import com.dimachine.core.annotation.Qualifier;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Annotation;

import static org.junit.jupiter.api.Assertions.*;

public class AnnotationUtilsTest {
    @Test
    public void shouldReturnFalse_whenPassingInAnEmptyArray() {
        Annotation[] annotations = {};

        assertFalse(AnnotationUtils.containsAnnotation(annotations, Qualifier.class));
    }

    @Test
    public void shouldReturnTrue_whenValueAnnotationIsPresentInAnnotationArray() throws NoSuchMethodException {
        Annotation[][] annotations = AnnotationUtilsTest.TestDummy.class.getDeclaredConstructor(String.class).getParameterAnnotations();

        assertTrue(AnnotationUtils.containsAnnotation(annotations[0], Qualifier.class));
    }

    @Test
    public void shouldReturnNull_whenEvaluatingAnEmptyArray() {
        Annotation[] annotations = {};

        assertNull(AnnotationUtils.getAnnotationValue(annotations, Qualifier.class));
    }

    @Test
    public void shouldBeAbleToGetConstructorParameterQualifiedName() throws NoSuchMethodException {
        Annotation[][] annotations = AnnotationUtilsTest.TestDummy.class.getDeclaredConstructor(String.class).getParameterAnnotations();

        assertEquals("funkyName", AnnotationUtils.getAnnotationValue(annotations[0], Qualifier.class));
    }

    private static class TestDummy {
        public TestDummy(@Qualifier("funkyName") String value) {
        }
    }
}
