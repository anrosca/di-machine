package com.dimachine.core.util;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

public class CollectionFactory {
    private static final Map<Class<?>, Supplier<Collection<?>>> collectionSuppliers = new HashMap<>();
    private static final Map<Class<?>, Supplier<Map<?, ?>>> mapSuppliers = new HashMap<>();

    static {
        collectionSuppliers.put(List.class, ArrayList::new);
        collectionSuppliers.put(Set.class, HashSet::new);

        mapSuppliers.put(Map.class, HashMap::new);
        mapSuppliers.put(ConcurrentMap.class, ConcurrentHashMap::new);
    }

    @SuppressWarnings("unchecked")
    public static <T> Collection<T> newCollectionOfType(Class<?> collectionType) {
        return (Collection<T>) Optional.ofNullable(collectionSuppliers.get(collectionType))
                .map(Supplier::get)
                .orElseThrow(() -> new UnsupportedCollectionException(collectionType + " is an unsupported collection type"));
    }

    @SuppressWarnings("unchecked")
    public static <K, V> Map<K, V> newMapOfType(Class<?> collectionType) {
        return (Map<K, V>) Optional.ofNullable(mapSuppliers.get(collectionType))
                .map(Supplier::get)
                .orElseThrow(() -> new UnsupportedCollectionException(collectionType + " is an unsupported map type"));
    }
}
