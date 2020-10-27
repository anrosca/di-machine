package com.dimachine.core.locator;

import com.dimachine.core.type.ClassMetadata;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RegExComponentFilterTest {

    @Test
    public void shouldBeAbleToMatchClassMetadataClassName_whenRegExMatches() {
        RegExComponentFilter filter = new RegExComponentFilter(new String[]{".*[ng]"});
        ClassMetadata classMetadata = mock(ClassMetadata.class);
        when(classMetadata.getClassName()).thenReturn("java.lang.String");

        assertTrue(filter.matches(classMetadata));
    }

    @Test
    public void shouldBeAbleToMatchClassMetadataClassName_whenAnyRegExMatches() {
        RegExComponentFilter filter = new RegExComponentFilter(new String[]{".*[ng]", ".*util.*"});
        ClassMetadata classMetadata = mock(ClassMetadata.class);
        when(classMetadata.getClassName()).thenReturn("java.util.List");

        assertTrue(filter.matches(classMetadata));
    }

    @Test
    public void whenThereAreNoRegularExpressions_shouldMatchAnyClass() {
        RegExComponentFilter filter = new RegExComponentFilter(new String[]{});
        ClassMetadata classMetadata = mock(ClassMetadata.class);
        when(classMetadata.getClassName()).thenReturn("java.util.List");

        assertTrue(filter.matches(classMetadata));
    }

    @Test
    public void shouldNotMatchClassMetadataClassName_whenRegExDoesNotMatch() {
        RegExComponentFilter filter = new RegExComponentFilter(new String[]{".*[ng]"});
        ClassMetadata classMetadata = mock(ClassMetadata.class);
        when(classMetadata.getClassName()).thenReturn("java.util.List");

        assertFalse(filter.matches(classMetadata));
    }
}
