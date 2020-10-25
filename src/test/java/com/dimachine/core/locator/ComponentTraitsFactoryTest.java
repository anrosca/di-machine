package com.dimachine.core.locator;

import com.dimachine.core.annotation.ComponentScan;
import com.dimachine.core.annotation.FilterType;
import com.dimachine.core.annotation.Service;
import com.dimachine.core.type.AnnotationMetadata;
import com.dimachine.core.type.ClassMetadata;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ComponentTraitsFactoryTest {

    static Stream<Arguments> makeComponentScanWithoutFiltersTestArguments() {
        return Stream.of(
                Arguments.of(ComponentScanWithValueAttribute.class, Set.of("java.lang")),
                Arguments.of(ComponentScanWithBasePackagesAttribute.class, Set.of("java.lang", "java.util")),
                Arguments.of(ComponentScanWithBasePackageClassesAttribute.class, Set.of("java.lang", "java.util")),
                Arguments.of(ComponentScanWithValueAndBasePackagesAttribute.class, Set.of("java.lang", "java.util", "java.time")),
                Arguments.of(ComponentScanWithAllPackageRelatedAttributes.class, Set.of("java.lang", "java.util", "java.time"))
        );
    }

    @MethodSource("makeComponentScanWithoutFiltersTestArguments")
    @ParameterizedTest(name = "Annotated class {0} with packages {1}")
    public void shouldBeAbleToReadPackagesFromComponentScanAnnotationWithoutFilters(Class<?> annotatedClass, Set<String> expectedPackages) {
        ComponentTraitsFactory traitsFactory = new ComponentTraitsFactory();
        ComponentScan componentScan = annotatedClass.getAnnotation(ComponentScan.class);

        ComponentTraits componentTraits = traitsFactory.from(componentScan);

        assertEquals(expectedPackages, Set.copyOf(componentTraits.getComponentPackages()));
        assertTrue(componentTraits.getComponentFilter().matches(mock(ClassMetadata.class)));
    }

    @Test
    public void shouldBeAbleToReadPackagesFromComponentScanAnnotationWithFilter() {
        ComponentTraitsFactory traitsFactory = new ComponentTraitsFactory();
        ComponentScan componentScan =
                ComponentScanWithValueAttributeAndAnnotationFilter.class.getAnnotation(ComponentScan.class);

        ComponentTraits componentTraits = traitsFactory.from(componentScan);

        assertEquals(List.of("java.lang"), componentTraits.getComponentPackages());
        assertTrue(componentTraits.getComponentFilter().matches(makeAnnotatedClassMetadata()));
    }

    @Test
    public void shouldBeAbleToReadPackagesFromComponentScanAnnotationWithRegExFilter() {
        ComponentTraitsFactory traitsFactory = new ComponentTraitsFactory();
        ComponentScan componentScan =
                ComponentScanWithValueAttributeAndRegExFilter.class.getAnnotation(ComponentScan.class);

        ComponentTraits componentTraits = traitsFactory.from(componentScan);

        assertEquals(List.of("java.lang"), componentTraits.getComponentPackages());
        assertTrue(componentTraits.getComponentFilter().matches(makeClassMetadataWithClassName("java.lang.String")));
    }

    @Test
    public void shouldBeAbleToReadPackagesFromComponentScanAnnotationWithCustomFilter() {
        ComponentTraitsFactory traitsFactory = new ComponentTraitsFactory();
        ComponentScan componentScan =
                ComponentScanWithValueAttributeAndCustomFilter.class.getAnnotation(ComponentScan.class);

        ComponentTraits componentTraits = traitsFactory.from(componentScan);

        assertEquals(List.of("java.lang"), componentTraits.getComponentPackages());
        assertTrue(componentTraits.getComponentFilter().matches(makeClassMetadataWithClassName("java.lang.String")));
    }

    private ClassMetadata makeClassMetadataWithClassName(String className) {
        ClassMetadata classMetadata = mock(ClassMetadata.class);
        when(classMetadata.getClassName()).thenReturn(className);
        return classMetadata;
    }

    private ClassMetadata makeAnnotatedClassMetadata() {
        ClassMetadata classMetadata = mock(ClassMetadata.class);
        AnnotationMetadata annotationMetadata = mock(AnnotationMetadata.class);
        when(classMetadata.getAnnotations()).thenReturn(List.of(annotationMetadata));
        when(annotationMetadata.getAnnotationClassName()).thenReturn(Service.class.getName());
        return classMetadata;
    }

    @ComponentScan("java.lang")
    private static class ComponentScanWithValueAttribute {
    }

    @ComponentScan(basePackages = {"java.lang", "java.util"})
    private static class ComponentScanWithBasePackagesAttribute {
    }

    @ComponentScan(basePackageClasses = {String.class, List.class})
    private static class ComponentScanWithBasePackageClassesAttribute {
    }

    @ComponentScan(value = {"java.lang", "java.util"}, basePackages = "java.time")
    private static class ComponentScanWithValueAndBasePackagesAttribute {
    }

    @ComponentScan(value = "java.util", basePackages = "java.time", basePackageClasses = String.class)
    private static class ComponentScanWithAllPackageRelatedAttributes {
    }

    @ComponentScan(basePackages = "java.lang",
            includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Service.class))
    private static class ComponentScanWithValueAttributeAndAnnotationFilter {
    }

    @ComponentScan(basePackages = "java.lang",
            includeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern = ".*[ng]"))
    private static class ComponentScanWithValueAttributeAndRegExFilter {
    }

    @ComponentScan(basePackages = "java.lang",
            includeFilters = @ComponentScan.Filter(type = FilterType.CUSTOM, classes = StringClassTypeFilter.class))
    private static class ComponentScanWithValueAttributeAndCustomFilter {
    }

    private static class StringClassTypeFilter implements TypeFilter {

        @Override
        public boolean match(MetadataReader metadataReader) {
            return metadataReader.getClassMetadata().getClassName().equals("java.lang.String");
        }
    }
}
