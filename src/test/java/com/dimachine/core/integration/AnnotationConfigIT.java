package com.dimachine.core.integration;

import com.dimachine.core.BeanScope;
import com.dimachine.core.DefaultBeanFactory;
import com.dimachine.core.annotation.*;
import com.dimachine.core.locator.MetadataReader;
import com.dimachine.core.locator.TypeFilter;
import filtering.test.AssignableTypeComponent;
import filtering.test.FilteredComponent;
import filtering.test.RegExMatchingComponent;
import filtering.test.WantedBean;
import org.junit.jupiter.api.Test;
import test.FooService;
import test.TestBean;

import java.io.Serializable;
import java.util.AbstractList;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
    public void shouldBeAbleToScanPackagesViaComponentScanningFiltering() throws Exception {
        try (DefaultBeanFactory beanFactory = new DefaultBeanFactory()) {
            beanFactory.register(ComponentScanningFilteringConfig.class);
            beanFactory.refresh();

            FilteredComponent bean = beanFactory.getBean(FilteredComponent.class);
            assertNotNull(bean);
        }
    }

    @Test
    public void shouldBeAbleToScanPackagesViaComponentScanningWithAssignableTypeFiltering() throws Exception {
        try (DefaultBeanFactory beanFactory = new DefaultBeanFactory()) {
            beanFactory.register(ComponentScanningAssignableTypeFilteringConfig.class);
            beanFactory.refresh();

            AssignableTypeComponent bean = beanFactory.getBean(AssignableTypeComponent.class);
            assertNotNull(bean);
        }
    }

    @Test
    public void shouldBeAbleToScanPackagesViaComponentScanningWithRegExFiltering() throws Exception {
        try (DefaultBeanFactory beanFactory = new DefaultBeanFactory()) {
            beanFactory.register(ComponentScanningRegExFilteringConfig.class);
            beanFactory.refresh();

            RegExMatchingComponent bean = beanFactory.getBean(RegExMatchingComponent.class);
            assertNotNull(bean);
        }
    }

    @Test
    public void shouldBeAbleToScanPackagesViaComponentScanningWithCustomFiltering() throws Exception {
        try (DefaultBeanFactory beanFactory = new DefaultBeanFactory()) {
            beanFactory.register(ComponentScanningWithCustomFilterConfig.class);
            beanFactory.refresh();

            FilteredComponent bean = beanFactory.getBean(FilteredComponent.class);
            assertNotNull(bean);
        }
    }

    @Configuration
    public static class AppConfiguration {

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
    }

    @Configuration
    @ComponentScan(basePackages = "filtering.test",
            includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = WantedBean.class))
    public static class ComponentScanningFilteringConfig {
    }

    @Configuration
    @ComponentScan(basePackages = "filtering.test",
            includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {Serializable.class, AbstractList.class}))
    public static class ComponentScanningAssignableTypeFilteringConfig {
    }

    @Configuration
    @ComponentScan(basePackages = "filtering.test",
            includeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern = ".*RegEx.*"))
    public static class ComponentScanningRegExFilteringConfig {
    }

    @Configuration
    @ComponentScan(basePackages = "filtering.test",
            includeFilters = @ComponentScan.Filter(type = FilterType.CUSTOM, classes = CustomFilterType.class))
    public static class ComponentScanningWithCustomFilterConfig {
    }

    private static class CustomFilterType implements TypeFilter {

        @Override
        public boolean match(MetadataReader metadataReader) {
            return metadataReader.getClassMetadata().getClassName().contains("FilteredComponent");
        }
    }
}
