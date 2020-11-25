package com.dimachine.core.proxy;

import com.dimachine.core.annotation.Bean;

import java.lang.reflect.Method;

public class IncludeBeanMethodsProxyMethodFilter implements ProxyMethodFilter {
    @Override
    public boolean isHandled(Method method) {
        return method.isAnnotationPresent(Bean.class);
    }
}
