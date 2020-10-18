package com.dimachine.core;

import com.dimachine.core.annotation.Scope;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ScopeResolverTest {

    private final ScopeResolver scopeResolver = new ScopeResolver();

    @Test
    public void shouldResolveBeanScopeToSingleton_WhenNoAnnotationsArePresent() {
        BeanScope resolvedScope = scopeResolver.resolveScope(TestBean.class.getName());

        assertEquals(BeanScope.SINGLETON, resolvedScope);
    }

    @Test
    public void shouldResolveBeanScopeToPrototype_whenScopeAnnotationHasValuePrototype() {
        BeanScope resolvedScope = scopeResolver.resolveScope(PrototypeTestBean.class.getName());

        assertEquals(BeanScope.PROTOTYPE, resolvedScope);
    }

    @Test
    public void shouldResolveBeanScopeToSingleton_whenScopeAnnotationHasValueSingleton() {
        BeanScope resolvedScope = scopeResolver.resolveScope(SingletonTestBean.class.getName());

        assertEquals(BeanScope.SINGLETON, resolvedScope);
    }

    @Test
    public void shouldThrowBeanClassMissingException_whenBeanClassCannotBeLoaded() {
        assertThrows(BeanClassMissingException.class, () -> scopeResolver.resolveScope("<yay>"));
    }

    private static class TestBean {
    }

    @Scope(BeanScope.SINGLETON)
    private static class SingletonTestBean {
    }

    @Scope(BeanScope.PROTOTYPE)
    private static class PrototypeTestBean {
    }
}
