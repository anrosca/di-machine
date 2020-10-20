package com.dimachine.core.util;

import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CollectionFactoryTest {

    @Test
    public void shouldGetAnArrayListWhenRequestingAList() {
        Collection<Object> list = CollectionFactory.newCollectionOfType(List.class);

        assertEquals(0, list.size());
        assertEquals(ArrayList.class, list.getClass());
    }

    @Test
    public void shouldGetAHashSetWhenRequestingASet() {
        Collection<Object> list = CollectionFactory.newCollectionOfType(Set.class);

        assertEquals(0, list.size());
        assertEquals(HashSet.class, list.getClass());
    }

    @Test
    public void shouldGetAHashMapWhenRequestingAMap() {
        Map<String, String> map = CollectionFactory.newMapOfType(Map.class);

        assertEquals(0, map.size());
        assertEquals(HashMap.class, map.getClass());
    }

    @Test
    public void shouldGetAConcurrentHashMapWhenRequestingAConcurrentMap() {
        Map<String, String> map = CollectionFactory.newMapOfType(ConcurrentMap.class);

        assertEquals(0, map.size());
        assertEquals(ConcurrentHashMap.class, map.getClass());
    }

    @Test
    public void shouldThrowUnsupportedCollectionException_whenRequestingUnknownCollectionType() {
        assertThrows(UnsupportedCollectionException.class, () -> CollectionFactory.newCollectionOfType(TreeMap.class));
    }

    @Test
    public void shouldThrowUnsupportedCollectionException_whenRequestingUnknownMapType() {
        assertThrows(UnsupportedCollectionException.class, () -> CollectionFactory.newMapOfType(TreeMap.class));
    }
}
