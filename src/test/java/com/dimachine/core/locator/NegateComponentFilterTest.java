package com.dimachine.core.locator;

import com.dimachine.core.type.ClassMetadata;
import org.junit.jupiter.api.Test;

import java.io.Serializable;
import java.util.List;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class NegateComponentFilterTest {

    @Test
    public void shouldBeAbleToNegateEmptyFilter() {
        NegateComponentFilter componentFilter = new NegateComponentFilter(new NoOpComponentFilter());
        ClassMetadata classMetadata = mock(ClassMetadata.class);

        assertFalse(componentFilter.matches(classMetadata));
    }

    @Test
    public void shouldBeAbleToNegateMatchingComponentFilter() {
        AssignableTypeComponentFilter assignableTypeFilter = new AssignableTypeComponentFilter(new Class<?>[]{Serializable.class});
        ClassMetadata classMetadata = mock(ClassMetadata.class);
        when(classMetadata.isSubclassOf("java.io.Serializable")).thenReturn(true);
        NegateComponentFilter componentFilter = new NegateComponentFilter(new OrComponentFilterCombiner(List.of(assignableTypeFilter)));

        assertFalse(componentFilter.matches(classMetadata));
    }

    @Test
    public void shouldMatchWhenUnderlyingFiltersDoNotMatch() {
        AssignableTypeComponentFilter assignableTypeFilter = new AssignableTypeComponentFilter(new Class<?>[]{Serializable.class, Supplier.class});
        ClassMetadata classMetadata = mock(ClassMetadata.class);
        when(classMetadata.isSubclassOf("java.io.Serializable")).thenReturn(false);
        when(classMetadata.isSubclassOf("java.util.function.Supplier")).thenReturn(false);

        NegateComponentFilter componentFilter = new NegateComponentFilter(new OrComponentFilterCombiner(List.of(assignableTypeFilter)));

        assertTrue(componentFilter.matches(classMetadata));
    }

    @Test
    public void test() {
        OrComponentFilterCombiner filter = new OrComponentFilterCombiner();
        AssignableTypeComponentFilter assignableTypeComponentFilter = new AssignableTypeComponentFilter(new Class<?>[]{Serializable.class});
        NegateComponentFilter negateComponentFilter = new NegateComponentFilter(new OrComponentFilterCombiner(List.of(assignableTypeComponentFilter)));
        filter.combineWith(negateComponentFilter);
        ClassMetadata classMetadata = mock(ClassMetadata.class);
        when(classMetadata.isSubclassOf("java.io.Serializable")).thenReturn(true);

        assertFalse(filter.matches(classMetadata));
    }
}
