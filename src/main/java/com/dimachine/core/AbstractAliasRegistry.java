package com.dimachine.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

public abstract class AbstractAliasRegistry implements AliasRegistry {
    private final Map<String, List<String>> aliases = new ConcurrentHashMap<>();

    @Override
    public void registerAlias(String beanName, String alias) {
        List<String> beanAliases = aliases.computeIfAbsent(beanName, (name) -> new ArrayList<>());
        beanAliases.add(alias);
    }

    @Override
    public void removeAlias(String alias) {
        for (List<String> beanAliases : aliases.values()) {
            boolean aliasRemoved = beanAliases.removeIf(currentAlias -> currentAlias.equals(alias));
            if (aliasRemoved)
                return;
        }
    }

    @Override
    public boolean isAlias(String alias) {
        return aliases.values()
                .stream()
                .flatMap(Collection::stream)
                .anyMatch(Predicate.isEqual(alias));
    }

    @Override
    public String[] getAliases(String beanName) {
        return aliases.getOrDefault(beanName, List.of())
                .toArray(String[]::new);
    }
}
