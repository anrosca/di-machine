package com.dimachine.core;

import _component.TestComponentWithExplicitName;
import _component.TestComponentWithoutExplicitName;
import _service.TestServiceWithExplicitName;
import _service.TestServiceWithoutExplicitName;
import com.dimachine.core.annotation.Component;
import com.dimachine.core.annotation.Service;
import com.dimachine.core.type.ClassMetadata;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ClasspathScannerTest {
    private final List<Class<? extends Annotation>> targetAnnotations = List.of(Component.class, Service.class);

    @Test
    public void givenNoPackages_shouldFindNothing() {
        ClasspathScanner scanner = new ClasspathScanner(targetAnnotations);
        List<ClassMetadata> foundBeanClassed = scanner.scan(List.of());

        assertEquals(0, foundBeanClassed.size());
    }

    @Test
    public void shouldFindComponentBeanDefinitionsFromGivenPackage() {
        ClasspathScanner scanner = new ClasspathScanner(targetAnnotations, "_component");
        List<ClassMetadata> foundBeanClassed = scanner.scan(List.of());

        Set<String> expectedClassNames = makeExpectedClassNames(TestComponentWithExplicitName.class, TestComponentWithoutExplicitName.class);
        assertEquals(expectedClassNames, makeActualResult(foundBeanClassed));
    }

    @Test
    public void shouldFindServiceBeanDefinitionsFromGivenPackage() {
        ClasspathScanner scanner = new ClasspathScanner(targetAnnotations, "_service");
        List<ClassMetadata> foundBeanClasses = scanner.scan(List.of());

        Set<String> expectedClassNames = makeExpectedClassNames(TestServiceWithExplicitName.class, TestServiceWithoutExplicitName.class);
        assertEquals(expectedClassNames, makeActualResult(foundBeanClasses));
    }

    @Test
    public void shouldFindServiceBeanDefinitionsFromAdditionalPackage() {
        ClasspathScanner scanner = new ClasspathScanner(targetAnnotations);

        List<ClassMetadata> foundBeanClasses = scanner.scan(List.of("_service"));

        Set<String> expectedClassNames = makeExpectedClassNames(TestServiceWithExplicitName.class, TestServiceWithoutExplicitName.class);
        assertEquals(expectedClassNames, makeActualResult(foundBeanClasses));
    }

    private Set<String> makeActualResult(List<ClassMetadata> foundBeanClasses) {
        return foundBeanClasses.stream()
                .map(ClassMetadata::getClassName)
                .collect(Collectors.toSet());
    }

    private Set<String> makeExpectedClassNames(Class<?>... expectedClasses) {
        return Arrays.stream(expectedClasses)
                .map(Class::getName)
                .collect(Collectors.toSet());
    }
}
