package com.dimachine.core.proxy;

import java.lang.reflect.Method;

public interface MethodInterceptor {
    Object invoke(Object proxyInstance, Method originalMethod, Method proxyMethod, Object[] args) throws Throwable;
}
