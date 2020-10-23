package com.dimachine.core.locator;

import com.dimachine.core.annotation.ComponentScan;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ComponentPackageLocatorTest {

    @Test
    public void whenGivenAnEmptyList_shouldReturnEmptyPackageList() {
        ComponentPackageLocator locator = new ComponentPackageLocator();

        ComponentTraits locatedPackages = locator.locate(List.of());

        assertEquals(0, locatedPackages.getComponentPackages().size());
    }

    @Test
    public void shouldBeAbleToReadComponentScanAnnotationWithSingleValue() {
        ComponentPackageLocator locator = new ComponentPackageLocator();

        ComponentTraits locatedPackages = locator.locate(List.of(SinglePackageConfig.class));

        assertEquals(List.of("com.example"), locatedPackages.getComponentPackages());
    }

    @Test
    public void shouldBeAbleToReadComponentScanAnnotationWithBasePackagesAttribute() {
        ComponentPackageLocator locator = new ComponentPackageLocator();

        ComponentTraits locatedPackages = locator.locate(List.of(BasePackagesConfig.class));

        assertEquals(Set.of("com.example", "evil.inc"), new HashSet<>(locatedPackages.getComponentPackages()));
    }

    @Test
    public void shouldBeAbleToReadComponentScanAnnotationWithTypeSafeBasePackagesAttribute() {
        ComponentPackageLocator locator = new ComponentPackageLocator();

        ComponentTraits locatedPackages = locator.locate(List.of(TypeSafeBasePackagesConfig.class));

        assertEquals(Set.of("java.lang", "java.util"), new HashSet<>(locatedPackages.getComponentPackages()));
    }

    @Test
    public void shouldBeAbleToReadComponentScanAnnotationWithAllAttributesPresent() {
        ComponentPackageLocator locator = new ComponentPackageLocator();

        ComponentTraits locatedPackages = locator.locate(List.of(CombinedAttributesConfig.class));

        assertEquals(Set.of("java.lang", "java.util", "com.example", "evil.inc"), new HashSet<>(locatedPackages.getComponentPackages()));
    }

    @Test
    public void shouldBeAbleToReadRepeatedComponentScanAnnotation() {
        ComponentPackageLocator locator = new ComponentPackageLocator();

        ComponentTraits locatedPackages = locator.locate(List.of(RepeatablePackagesConfig.class));

        assertEquals(Set.of("java.lang", "java.util"), new HashSet<>(locatedPackages.getComponentPackages()));
    }

    private static ComponentTraits makeTraitsWith(String... packages) {
        return new ComponentTraits(List.of(packages));
    }

    @ComponentScan("com.example")
    private static class SinglePackageConfig {
    }

    @ComponentScan(basePackages = {"com.example", "evil.inc"})
    private static class BasePackagesConfig {
    }

    @ComponentScan(basePackageClasses = {String.class, List.class})
    private static class TypeSafeBasePackagesConfig {
    }

    @ComponentScan(basePackageClasses = String.class)
    @ComponentScan(basePackageClasses = List.class)
    private static class RepeatablePackagesConfig {
    }

    @ComponentScan(value = "com.example", basePackages = {"evil.inc"}, basePackageClasses = {String.class, List.class})
    private static class CombinedAttributesConfig {
    }
}
