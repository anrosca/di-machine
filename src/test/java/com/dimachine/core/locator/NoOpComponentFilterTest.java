package com.dimachine.core.locator;

import com.dimachine.core.type.ClassMetadata;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

public class NoOpComponentFilterTest {

    @Test
    public void shouldMatchEveryClassMetadata() {
        ClassMetadata classMetadata = mock(ClassMetadata.class);
        NoOpComponentFilter noOpComponentFilter = new NoOpComponentFilter();

        assertTrue(noOpComponentFilter.matches(classMetadata));
    }

    @Test
    public void everyNoOpFilterShouldBeEqualToEveryOtherInstance() {
        NoOpComponentFilter firstFilter = new NoOpComponentFilter();
        NoOpComponentFilter secondFilter = new NoOpComponentFilter();

        assertEquals(secondFilter, firstFilter);
        assertEquals(firstFilter.hashCode(), secondFilter.hashCode());
    }

    @Test
    public void everyNoOpFilterShouldNotBeEqualToNull() {
        NoOpComponentFilter noOpComponentFilter = new NoOpComponentFilter();

        assertNotEquals(noOpComponentFilter, null);
    }
}
