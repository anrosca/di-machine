package com.dimachine.core;

import com.dimachine.core.annotation.Bean;
import com.dimachine.core.annotation.Configuration;
import com.dimachine.core.annotation.Scope;
import com.dimachine.core.proxy.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import test.FooService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

public class DefaultProxyFactoryTest {
    private static List<String> invocations;

    private final DefaultProxyFactory proxyFactory = new DefaultProxyFactory();
    private final DefaultBeanFactory beanFactory = mock(DefaultBeanFactory.class);
    private final ConfigurationClassInvocationHandler invocationHandler = new ConfigurationClassInvocationHandler(beanFactory);

    @BeforeEach
    void setUp() {
        invocations = new ArrayList<>();
    }

    @Test
    public void prototypeBeanMethodsShouldBeCalledOnEveryInvocation() {
        ProxyTraits proxyTraits = ProxyTraits.builder()
                .superClass(AppConfig.class)
                .methodInterceptor(invocationHandler)
                .methodFilter(new IgnoreObjectMethodsMethodFilter().and(new IncludeBeanMethodsProxyMethodFilter()))
                .build();
        AppConfig proxiedConfigInstance = (AppConfig) proxyFactory.makeProxy(proxyTraits);

        assertNotNull(proxiedConfigInstance.prototypeService());
        assertNotNull(proxiedConfigInstance.prototypeService());
        assertNotNull(proxiedConfigInstance.prototypeService());

        assertEquals("prototype;prototype;prototype", String.join(";", invocations));
        verify(beanFactory, never()).getBean(FooService.class);
    }

    @Test
    public void singletonBeanMethodsShouldBeCalledOnlyOnce() {
        when(beanFactory.getBean("singletonService", FooService.class)).thenReturn(new FooService());

        ProxyTraits proxyTraits = ProxyTraits.builder()
                .superClass(AppConfig.class)
                .methodInterceptor(invocationHandler)
                .methodFilter(new IgnoreObjectMethodsMethodFilter().and(new IncludeBeanMethodsProxyMethodFilter()))
                .build();
        AppConfig proxiedConfigInstance = (AppConfig) proxyFactory.makeProxy(proxyTraits);

        assertNotNull(proxiedConfigInstance.singletonService());
        assertNotNull(proxiedConfigInstance.singletonService());
        assertNotNull(proxiedConfigInstance.singletonService());

        assertEquals("singleton", String.join(";", invocations));
        verify(beanFactory, times(2)).getBean("singletonService", FooService.class);
    }

    @Test
    public void methodsWhichAreNotAnnotatedWithBean_shouldNotBeIntercepted() {
        ProxyTraits proxyTraits = ProxyTraits.builder()
                .superClass(AppConfig.class)
                .methodInterceptor(invocationHandler)
                .methodFilter(new IgnoreObjectMethodsMethodFilter().and(new IncludeBeanMethodsProxyMethodFilter()))
                .build();
        AppConfig proxiedConfigInstance = (AppConfig) proxyFactory.makeProxy(proxyTraits);

        proxiedConfigInstance.setValue();
        proxiedConfigInstance.setValue();

        assertEquals("setValue;setValue", String.join(";", invocations));
        verifyNoInteractions(beanFactory);
    }

    @Configuration
    public static class AppConfig {

        @Scope(BeanScope.PROTOTYPE)
        @Bean
        public FooService prototypeService() {
            invocations.add("prototype");
            return new FooService();
        }

        @Scope(BeanScope.SINGLETON)
        @Bean
        public FooService singletonService() {
            invocations.add("singleton");
            return new FooService();
        }

        public void setValue() {
            invocations.add("setValue");
        }
    }
}
