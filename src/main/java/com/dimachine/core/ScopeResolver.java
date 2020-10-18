package com.dimachine.core;

import com.dimachine.core.annotation.Scope;

public class ScopeResolver {
    public BeanScope resolveScope(String className) {
        try {
            return tryResolveScope(className);
        } catch (ClassNotFoundException e) {
            throw new BeanClassMissingException("Bean class " + className + " could not be found", e);
        }
    }

    private BeanScope tryResolveScope(String className) throws ClassNotFoundException {
        Class<?> beanClass = Class.forName(className);
        if (beanClass.isAnnotationPresent(Scope.class)) {
            Scope scopeAnnotation = beanClass.getAnnotation(Scope.class);
            return scopeAnnotation.value();
        }
        return BeanScope.SINGLETON;
    }
}
