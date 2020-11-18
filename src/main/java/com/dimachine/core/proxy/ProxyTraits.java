package com.dimachine.core.proxy;

import static com.dimachine.core.proxy.ProxyMethodFilter.ACCEPT_ALL_METHODS_METHOD_FILTER;

public class ProxyTraits {
    private final Class<?> superClass;
    private final Object[] constructorArguments;
    private final MethodInterceptor methodInterceptor;
    private final ProxyMethodFilter methodFilter;

    private ProxyTraits(ProxyTraitsBuilder builder) {
        this.superClass = builder.superClass;
        this.constructorArguments = builder.constructorArguments;
        this.methodInterceptor = builder.methodInterceptor;
        this.methodFilter = builder.methodFilter;
    }

    public Class<?> getSuperClass() {
        return superClass;
    }

    public Object[] getConstructorArguments() {
        return constructorArguments;
    }

    public MethodInterceptor getMethodInterceptor() {
        return methodInterceptor;
    }

    public ProxyMethodFilter getMethodFilter() {
        return methodFilter;
    }

    public static ProxyTraitsBuilder builder() {
        return new ProxyTraitsBuilder();
    }

    public static class ProxyTraitsBuilder {
        private Class<?> superClass;
        private Object[] constructorArguments = new Object[]{};
        private MethodInterceptor methodInterceptor;
        private ProxyMethodFilter methodFilter = ACCEPT_ALL_METHODS_METHOD_FILTER;

        public ProxyTraitsBuilder superClass(Class<?> superClass) {
            this.superClass = superClass;
            return this;
        }

        public ProxyTraitsBuilder constructorArguments(Object[] constructorArguments) {
            this.constructorArguments = constructorArguments;
            return this;
        }

        public ProxyTraitsBuilder methodInterceptor(MethodInterceptor methodInterceptor) {
            this.methodInterceptor = methodInterceptor;
            return this;
        }

        public ProxyTraitsBuilder methodFilter(ProxyMethodFilter methodFilter) {
            this.methodFilter = methodFilter;
            return this;
        }

        public ProxyTraits build() {
            return new ProxyTraits(this);
        }
    }
}
