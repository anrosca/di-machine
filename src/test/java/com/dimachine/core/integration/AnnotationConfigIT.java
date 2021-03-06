package com.dimachine.core.integration;

import com.dimachine.core.BeanCurrentlyInCreationException;
import com.dimachine.core.BeanScope;
import com.dimachine.core.DefaultBeanFactory;
import com.dimachine.core.annotation.*;
import org.junit.jupiter.api.Test;
import test.FooService;
import test.TestBean;

import static org.junit.jupiter.api.Assertions.*;

public class AnnotationConfigIT {

    @Test
    public void shouldBeAbleToGetBeansViaJavaConfiguration() throws Exception {
        TestBean bean;
        try (DefaultBeanFactory beanFactory = new DefaultBeanFactory(AppConfiguration.class)) {
            beanFactory.refresh();

            bean = beanFactory.getBean("testBean", TestBean.class);
            assertTrue(bean.initMethodWasCalled());
            assertNotNull(bean.getAutowiredField());
            assertNotNull(bean.getAutowireList());
            assertNotNull(bean.getAutowireMap());
        }
        assertTrue(bean.destroyMethodWasCalled());
        assertTrue(bean.annotatedDestroyMethodWasCalled());
    }

    @Test
    public void shouldBeAbleToGetBeansViaQualifierName() throws Exception {
        DefaultBeanFactory beanFactory = new DefaultBeanFactory(AppConfiguration.class);
        beanFactory.refresh();

        TestBean bean = (TestBean) beanFactory.getBean("test_bean_alias");
        assertNotNull(bean);
    }

    @Test
    public void shouldBeAbleToAutowireFieldsInBeanPostProcessors() throws Exception {
        DefaultBeanFactory beanFactory = new DefaultBeanFactory(ComponentScanningConfig.class);
        beanFactory.refresh();

        ComponentScanningConfig bean = beanFactory.getBean(ComponentScanningConfig.class);
        assertNotNull(bean.testBean);
    }

    @Test
    public void shouldBeAbleToGetPrototypeBeansViaJavaConfiguration() throws Exception {
        TestBean bean;
        try (DefaultBeanFactory beanFactory = new DefaultBeanFactory(PrototypeAppConfig.class)) {
            beanFactory.refresh();

            bean = beanFactory.getBean(TestBean.class);
            assertTrue(bean.initMethodWasCalled());
            assertNotNull(bean.getAutowiredField());
            assertNotNull(bean.getAutowireList());
            assertNotNull(bean.getAutowireMap());
        }
    }

    @Test
    public void shouldBeAbleToGetPrototypeBeansWithSingletonDependenciesViaJavaConfiguration() throws Exception {
        TestBean bean;
        try (DefaultBeanFactory beanFactory = new DefaultBeanFactory(PrototypeWithSingletonDependenciesConfig.class)) {
            beanFactory.refresh();

            bean = beanFactory.getBean(TestBean.class);
            assertTrue(bean.initMethodWasCalled());
            assertNotNull(bean.getAutowiredField());
            assertNotNull(bean.getAutowireList());
            assertNotNull(bean.getAutowireMap());
        }
    }

    @Test
    public void shouldBeAbleToGetPrototypeBeansWithPrototypeDependenciesViaJavaConfiguration() throws Exception {
        TestBean bean;
        try (DefaultBeanFactory beanFactory = new DefaultBeanFactory(PrototypeWithPrototypeDependenciesConfig.class)) {
            beanFactory.refresh();

            bean = beanFactory.getBean(TestBean.class);
            assertTrue(bean.initMethodWasCalled());
            assertNotNull(bean.getAutowiredField());
            assertNotNull(bean.getAutowireList());
            assertNotNull(bean.getAutowireMap());
        }
    }

    @Test
    public void shouldBeAbleToInjectPrototypeBeansInSingletonsViaJavaConfiguration() throws Exception {
        TestBean bean;
        try (DefaultBeanFactory beanFactory = new DefaultBeanFactory(InjectPrototypeIntoSingletonAppConfig.class)) {
            beanFactory.refresh();

            bean = beanFactory.getBean(TestBean.class);
            assertTrue(bean.initMethodWasCalled());
            assertNotNull(bean.getAutowiredField());
            assertNotNull(bean.getAutowireList());
            assertNotNull(bean.getAutowireMap());
        }
    }

    @Test
    public void shouldBeAbleToScanPackagesViaComponentScanning() throws Exception {
        TestBean bean;
        try (DefaultBeanFactory beanFactory = new DefaultBeanFactory()) {
            beanFactory.register(ComponentScanningConfig.class);
            beanFactory.refresh();

            bean = beanFactory.getBean("testBean", TestBean.class);
            assertTrue(bean.initMethodWasCalled());
            assertNotNull(bean.getAutowiredField());
            assertNotNull(bean.getAutowireList());
            assertNotNull(bean.getAutowireMap());
        }
        assertTrue(bean.destroyMethodWasCalled());
        assertTrue(bean.annotatedDestroyMethodWasCalled());
    }

    @Test
    public void shouldThrow_whenThereAreCyclicPrototypeBeans() throws Exception {
        try (DefaultBeanFactory beanFactory = new DefaultBeanFactory()) {
            beanFactory.register(PrototypeWithCyclesConfig.class);
            beanFactory.refresh();

            Exception exception = assertThrows(BeanCurrentlyInCreationException.class, () -> beanFactory.getBean(TestBean.class));
            assertEquals("Cyclic dependency between bean with name 'prototypeTestBean' and type " +
                    "class test.TestBean and bean with name 'fooService' with type class test.FooService", exception.getMessage());
        }
    }

    @Configuration
    public static class AppConfiguration {

        @Qualifier("test_bean_alias")
        @Bean
        public TestBean testBean(FooService fooService) {
            return new TestBean(fooService);
        }

        @Bean
        public FooService fooService() {
            System.out.println("yay");
            return new FooService();
        }
    }

    @Configuration
    public static class PrototypeAppConfig {
        @Scope(BeanScope.PROTOTYPE)
        @Bean
        public TestBean prototypeTestBean() {
            return new TestBean(new FooService());
        }

        @Bean
        public FooService fooService() {
            return new FooService();
        }
    }

    @Configuration
    public static class PrototypeWithSingletonDependenciesConfig {
        @Scope(BeanScope.PROTOTYPE)
        @Bean
        public TestBean prototypeTestBean(FooService fooService) {
            return new TestBean(fooService);
        }

        @Bean
        public FooService fooService() {
            return new FooService();
        }
    }

    @Configuration
    public static class PrototypeWithPrototypeDependenciesConfig {
        @Scope(BeanScope.PROTOTYPE)
        @Bean
        public TestBean prototypeTestBean(FooService fooService) {
            return new TestBean(fooService);
        }

        @Scope(BeanScope.PROTOTYPE)
        @Bean
        public FooService fooService() {
            return new FooService();
        }
    }

    @Configuration
    public static class PrototypeWithCyclesConfig {
        @Scope(BeanScope.PROTOTYPE)
        @Bean
        public TestBean prototypeTestBean(FooService fooService) {
            return new TestBean(fooService);
        }

        @Scope(BeanScope.PROTOTYPE)
        @Bean
        public FooService fooService(TestBean testBean) {
            return new FooService();
        }
    }

    @Configuration
    public static class InjectPrototypeIntoSingletonAppConfig {

        @Bean
        public TestBean testBean(FooService fooService) {
            return new TestBean(fooService);
        }

        @Scope(BeanScope.PROTOTYPE)
        @Bean
        public FooService fooService() {
            return new FooService();
        }
    }

    @Configuration
    @ComponentScan("test")
    public static class ComponentScanningConfig {
        @Autowired
        TestBean testBean;
    }
}
