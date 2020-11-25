package com.dimachine.core.proxy;

import java.lang.reflect.Method;

public interface ProxyMethodFilter {
    boolean isHandled(Method method);

    default ProxyMethodFilter and(ProxyMethodFilter otherFilter) {
        return (method) -> isHandled(method) && otherFilter.isHandled(method);
    }

    ProxyMethodFilter ACCEPT_ALL_METHODS_METHOD_FILTER = (method) -> true;
}
