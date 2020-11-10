package com.dimachine.core.integration;

import com.dimachine.core.BeanFactory;
import com.dimachine.core.BeanScope;
import com.dimachine.core.DefaultBeanFactory;
import com.dimachine.core.annotation.Bean;
import com.dimachine.core.annotation.Configuration;
import com.dimachine.core.annotation.Scope;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import test.FooService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MultipleInvocationsOfBeanAnnotatedMethodsIT {
    private static List<String> invocations = new ArrayList<>();

    @BeforeEach
    void setUp() {
        invocations = new ArrayList<>();
    }

    @Test
    public void singletonMethodShouldBeCalledOnlyOnce() {
        BeanFactory beanFactory = new DefaultBeanFactory(SingletonAppConfig.class);
        beanFactory.refresh();

        beanFactory.getBean("yetAnotherService");

        assertEquals("singleton", String.join(";", invocations));
    }

    @Test
    public void whenLightModeIsOn_singletonMethodShouldBeCalledAsNeeded() {
        BeanFactory beanFactory = new DefaultBeanFactory(SingletonAppConfigInLightMode.class);
        beanFactory.refresh();

        beanFactory.getBean("yetAnotherService");

        assertEquals("singleton;singleton;singleton", String.join(";", invocations));
    }

    @Test
    public void prototypeMethodShouldBeCalledWhenWanted() {
        BeanFactory beanFactory = new DefaultBeanFactory(PrototypeAppConfig.class);
        beanFactory.refresh();

        beanFactory.getBean("prototypeService");
        beanFactory.getBean("prototypeService");
        beanFactory.getBean("prototypeService");

        assertEquals("prototype;prototype;prototype", String.join(";", invocations));
    }

    @Configuration
    public static class PrototypeAppConfig {

        @Scope(BeanScope.PROTOTYPE)
        @Bean
        public FooService prototypeService() {
            invocations.add("prototype");
            return new FooService();
        }
    }

    @Configuration
    public static class SingletonAppConfig {

        @Bean
        public FooService singletonService() {
            invocations.add("singleton");
            return new FooService();
        }

        @Bean
        public FooService yetAnotherService() {
            singletonService();
            singletonService();
            return new FooService();
        }
    }

    @Configuration(proxyBeanMethods = false)
    public static class SingletonAppConfigInLightMode {

        @Bean
        public FooService singletonService() {
            invocations.add("singleton");
            return new FooService();
        }

        @Bean
        public FooService yetAnotherService() {
            singletonService();
            singletonService();
            return new FooService();
        }
    }
}
