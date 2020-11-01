package com.dimachine.core;

public interface AliasRegistry {

    void registerAlias(String name, String alias);

    void removeAlias(String alias);

    boolean isAlias(String alias);

    String[] getAliases(String name);
}
