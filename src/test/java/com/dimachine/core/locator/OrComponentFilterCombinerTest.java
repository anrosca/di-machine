package com.dimachine.core.locator;

import com.dimachine.core.type.ClassMetadata;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class OrComponentFilterCombinerTest {

    @Test
    public void emptyFilterCombinerShouldMatchAnyClassMetadata() {
        OrComponentFilterCombiner filterCombiner = new OrComponentFilterCombiner();
        ClassMetadata classMetadata = mock(ClassMetadata.class);

        assertTrue(filterCombiner.matches(classMetadata));
    }

    @Test
    public void shouldConsultWrappedFiltersAboutMatching() {
        ComponentFilter filter = mock(ComponentFilter.class);
        ClassMetadata classMetadata = mock(ClassMetadata.class);
        when(filter.matches(classMetadata)).thenReturn(true);
        OrComponentFilterCombiner filterCombiner = new OrComponentFilterCombiner(List.of(filter));

        assertTrue(filterCombiner.matches(classMetadata));
        verify(filter).matches(classMetadata);
    }

    @Test
    public void shouldMatchIfAnyWrappedFiltersTellsSo() {
        ComponentFilter firstFilter = mock(ComponentFilter.class);
        ComponentFilter secondFilter = mock(ComponentFilter.class);
        ClassMetadata classMetadata = mock(ClassMetadata.class);
        when(firstFilter.matches(classMetadata)).thenReturn(true);
        when(secondFilter.matches(classMetadata)).thenReturn(false);
        OrComponentFilterCombiner filterCombiner = new OrComponentFilterCombiner(List.of(firstFilter, secondFilter));

        assertTrue(filterCombiner.matches(classMetadata));
        verify(firstFilter).matches(classMetadata);
        verify(secondFilter).matches(classMetadata);
    }

    @Test
    public void shouldBeAbleToCombineFilters() {
        OrComponentFilterCombiner filterCombiner = new OrComponentFilterCombiner();
        ClassMetadata classMetadata = mock(ClassMetadata.class);
        ComponentFilter filter = mock(ComponentFilter.class);
        when(filter.matches(classMetadata)).thenReturn(false);

        filterCombiner.combineWith(filter);

        assertFalse(filterCombiner.matches(classMetadata));
    }
}
