package com.dimachine.core.util;

import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static org.junit.jupiter.api.Assertions.*;

public class CollectionUtilsTest {

    @Test
    public void shouldGetAnArrayListWhenRequestingAList() {
        Collection<Object> list = CollectionUtils.newCollectionOfType(List.class);

        assertEquals(0, list.size());
        assertEquals(ArrayList.class, list.getClass());
    }

    @Test
    public void shouldGetAHashSetWhenRequestingASet() {
        Collection<Object> list = CollectionUtils.newCollectionOfType(Set.class);

        assertEquals(0, list.size());
        assertEquals(HashSet.class, list.getClass());
    }

    @Test
    public void shouldGetAHashMapWhenRequestingAMap() {
        Map<String, String> map = CollectionUtils.newMapOfType(Map.class);

        assertEquals(0, map.size());
        assertEquals(HashMap.class, map.getClass());
    }

    @Test
    public void shouldGetAConcurrentHashMapWhenRequestingAConcurrentMap() {
        Map<String, String> map = CollectionUtils.newMapOfType(ConcurrentMap.class);

        assertEquals(0, map.size());
        assertEquals(ConcurrentHashMap.class, map.getClass());
    }

    @Test
    public void shouldThrowUnsupportedCollectionException_whenRequestingUnknownCollectionType() {
        assertThrows(UnsupportedCollectionException.class, () -> CollectionUtils.newCollectionOfType(TreeMap.class));
    }

    @Test
    public void shouldThrowUnsupportedCollectionException_whenRequestingUnknownMapType() {
        assertThrows(UnsupportedCollectionException.class, () -> CollectionUtils.newMapOfType(TreeMap.class));
    }

    @Test
    public void shouldIdentifyArrayListAsACollection() {
        assertTrue(CollectionUtils.isCollection(ArrayList.class));
    }

    @Test
    public void shouldIdentifyHashSetAsACollection() {
        assertTrue(CollectionUtils.isCollection(HashSet.class));
    }

    @Test
    public void shouldIdentifyArrayBlockingQueueAsACollection() {
        assertTrue(CollectionUtils.isCollection(ArrayBlockingQueue.class));
    }

    @Test
    public void shouldIdentifyHashMapAsAMap() {
        assertTrue(CollectionUtils.isMap(HashMap.class));
    }

    @Test
    public void shouldIdentifyConcurrentHashMapAsAMap() {
        assertTrue(CollectionUtils.isMap(ConcurrentHashMap.class));
    }
}
