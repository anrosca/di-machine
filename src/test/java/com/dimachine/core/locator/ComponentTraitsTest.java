package com.dimachine.core.locator;

import com.dimachine.core.type.ClassMetadata;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ComponentTraitsTest {

    @Test
    public void newlyConstructedComponentTraitsWithoutFilter_shouldMatchAnyClassMetadata() {
        ComponentTraits componentTraits = new ComponentTraits(List.of("java.lang"));

        assertTrue(componentTraits.getComponentFilter().matches(mock(ClassMetadata.class)));
    }

    @Test
    public void newlyConstructedComponentTraits_shouldRememberGivenPackages() {
        ComponentTraits componentTraits = new ComponentTraits(List.of("java.lang"));

        assertEquals(List.of("java.lang"), componentTraits.getComponentPackages());
    }

    @Test
    public void shouldBeAbleToCombineTraits() {
        ComponentTraits componentTraits = new ComponentTraits(List.of("java.lang"));

        componentTraits.addTrait(new ComponentTraits(List.of("java.util")));

        assertEquals(List.of("java.lang", "java.util"), componentTraits.getComponentPackages());
    }

    @Test
    public void whenCombiningTraits_theirFiltersShouldBeCombined() {
        ComponentFilter firstFilter = mock(ComponentFilter.class);
        ComponentFilter secondFilter = mock(ComponentFilter.class);
        ComponentTraits componentTraits = new ComponentTraits(List.of("java.lang"), firstFilter);
        ClassMetadata classMetadata = mock(ClassMetadata.class);
        when(firstFilter.matches(classMetadata)).thenReturn(true);
        when(secondFilter.matches(classMetadata)).thenReturn(false);

        componentTraits.addTrait(new ComponentTraits(List.of("java.util"), secondFilter));

        assertTrue(componentTraits.getComponentFilter().matches(classMetadata));
        verify(firstFilter).matches(classMetadata);
        verify(secondFilter).matches(classMetadata);
    }

    @Test
    public void componentTraitsWithSameState_shouldBeEqual() {
        ComponentTraits firstTrait = new ComponentTraits(List.of("java.lang"));
        ComponentTraits secondTrait = new ComponentTraits(List.of("java.lang"));

        assertEquals(firstTrait, secondTrait);
    }

    @Test
    public void componentTraitsWithDifferentFilters_shouldNotBeEqual() {
        ComponentTraits firstTrait = new ComponentTraits(List.of("java.lang"));
        ComponentTraits secondTrait = new ComponentTraits(List.of("java.lang"), mock(ComponentFilter.class));

        assertNotEquals(firstTrait, secondTrait);
    }

    @Test
    public void componentTraitsWithDifferentPackages_shouldNotBeEqual() {
        ComponentTraits firstTrait = new ComponentTraits(List.of("java.lang"));
        ComponentTraits secondTrait = new ComponentTraits(List.of("java.util"));

        assertNotEquals(firstTrait, secondTrait);
    }

    @Test
    public void componentTraitsWithSameState_shouldHaveEqualHashCode() {
        ComponentTraits firstTrait = new ComponentTraits(List.of("java.lang"));
        ComponentTraits secondTrait = new ComponentTraits(List.of("java.lang"));

        assertEquals(firstTrait.hashCode(), secondTrait.hashCode());
    }

    @Test
    public void componentTraitsWithDifferentFilters_shouldHaveDifferentHashCodes() {
        ComponentTraits firstTrait = new ComponentTraits(List.of("java.lang"));
        ComponentTraits secondTrait = new ComponentTraits(List.of("java.lang"), mock(ComponentFilter.class));

        assertNotEquals(firstTrait.hashCode(), secondTrait.hashCode());
    }

    @Test
    public void componentTraitsWithDifferentPackages_shouldHaveDifferentHashCodes() {
        ComponentTraits firstTrait = new ComponentTraits(List.of("java.lang"));
        ComponentTraits secondTrait = new ComponentTraits(List.of("java.util"));

        assertNotEquals(firstTrait.hashCode(), secondTrait.hashCode());
    }
}
