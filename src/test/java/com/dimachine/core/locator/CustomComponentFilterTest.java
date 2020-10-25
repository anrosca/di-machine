package com.dimachine.core.locator;

import com.dimachine.core.type.ClassMetadata;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CustomComponentFilterTest {

    @Test
    public void shouldMatchWhenCustomTypeFiltersAreProvided() {
        CustomComponentFilter filter = new CustomComponentFilter(new Class[]{});
        ClassMetadata classMetadata = mock(ClassMetadata.class);

        assertTrue(filter.matches(classMetadata));
    }

    @Test
    public void shouldMatchWhenCustomTypeFilterReturnsAMatch() {
        TypeFilter customFilter = (metadataReader -> metadataReader.getClassMetadata().getClassName().equals("java.lang.String"));
        CustomComponentFilter filter = new CustomComponentFilter(new Class[]{customFilter.getClass()});
        ClassMetadata classMetadata = mock(ClassMetadata.class);
        when(classMetadata.getClassName()).thenReturn("java.lang.String");

        assertTrue(filter.matches(classMetadata));
    }

    @Test
    public void shouldMatchWhenAnyCustomTypeFilterMatches() {
        TypeFilter rejectingFilter = metadataReader -> false;
        TypeFilter matchingFilter = metadataReader -> metadataReader.getClassMetadata().getClassName().length() == 16;
        CustomComponentFilter filter = new CustomComponentFilter(new Class[]{rejectingFilter.getClass(), matchingFilter.getClass()});
        ClassMetadata classMetadata = mock(ClassMetadata.class);
        when(classMetadata.getClassName()).thenReturn("java.lang.String");

        assertTrue(filter.matches(classMetadata));
    }

    @Test
    public void shouldNotMatchWhenAllCustomTypeFiltersDoNotMatch() {
        TypeFilter rejectingFilter = metadataReader -> false;
        TypeFilter anotherRejectingFilter = metadataReader -> false;
        CustomComponentFilter filter = new CustomComponentFilter(new Class[]{rejectingFilter.getClass(), anotherRejectingFilter.getClass()});
        ClassMetadata classMetadata = mock(ClassMetadata.class);
        when(classMetadata.getClassName()).thenReturn("java.lang.String");

        assertFalse(filter.matches(classMetadata));
    }

    @Test
    public void shouldThrowIncorrectTypeFilterClassException_whenCustomFilterHasIncompatibleType() {
        IncorrectTypeFilterClassException exception = assertThrows(IncorrectTypeFilterClassException.class,
                () -> new CustomComponentFilter(new Class[]{ArrayList.class}));
        assertEquals("class java.util.ArrayList is expected to implement " +
                TypeFilter.class.getName() + " interface.", exception.getMessage());
    }
}
