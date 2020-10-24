package com.dimachine.core.locator;

import com.dimachine.core.type.ClassMetadata;
import org.junit.jupiter.api.Test;

import java.io.Serializable;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AssignableTypeComponentFilterTest {

    @Test
    public void shouldMatchOnlyTargetClasses() {
        AssignableTypeComponentFilter filter = new AssignableTypeComponentFilter(new Class<?>[]{Serializable.class});
        ClassMetadata classMetadata = mock(ClassMetadata.class);
        when(classMetadata.isSubclassOf("java.io.Serializable")).thenReturn(true);

        assertTrue(filter.matches(classMetadata));
    }

    @Test
    public void shouldMatchIfAnyTargetClassIsAnAssignableType() {
        AssignableTypeComponentFilter filter = new AssignableTypeComponentFilter(new Class<?>[]{Serializable.class, Supplier.class});
        ClassMetadata classMetadata = mock(ClassMetadata.class);
        when(classMetadata.isSubclassOf("java.io.Serializable")).thenReturn(false);
        when(classMetadata.isSubclassOf("java.util.function.Supplier")).thenReturn(true);

        assertTrue(filter.matches(classMetadata));
    }

    @Test
    public void shouldNoMatchIfAllTargetTypesAreNotAssignableTypes() {
        AssignableTypeComponentFilter filter = new AssignableTypeComponentFilter(new Class<?>[]{Serializable.class, Supplier.class});
        ClassMetadata classMetadata = mock(ClassMetadata.class);
        when(classMetadata.isSubclassOf("java.io.Serializable")).thenReturn(false);
        when(classMetadata.isSubclassOf("java.util.function.Supplier")).thenReturn(false);

        assertFalse(filter.matches(classMetadata));
    }
}
