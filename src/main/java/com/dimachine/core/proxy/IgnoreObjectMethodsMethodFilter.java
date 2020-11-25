package com.dimachine.core.proxy;

import java.lang.reflect.Method;
import java.util.Set;

public class IgnoreObjectMethodsMethodFilter implements ProxyMethodFilter {
    private static final Set<String> bypassedMethods = Set.of(
            "equals",
            "hashCode",
            "wait",
            "notify",
            "notifyAll",
            "getClass",
            "finalize",
            "toString",
            "clone"
    );

    @Override
    public boolean isHandled(Method method) {
        return !bypassedMethods.contains(method.getName());
    }
}
