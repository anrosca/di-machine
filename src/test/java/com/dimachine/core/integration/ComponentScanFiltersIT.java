package com.dimachine.core.integration;

import com.dimachine.core.DefaultBeanFactory;
import com.dimachine.core.NoSuchBeanDefinitionException;
import com.dimachine.core.annotation.ComponentScan;
import com.dimachine.core.annotation.ComponentScan.FilterType;
import com.dimachine.core.annotation.Configuration;
import com.dimachine.core.locator.MetadataReader;
import com.dimachine.core.locator.TypeFilter;
import exclude.filtering.test.ExcludedComponent;
import exclude.filtering.test.IterableComponent;
import exclude.filtering.test.UnwantedBean;
import include.filtering.test.AssignableTypeComponent;
import include.filtering.test.FilteredComponent;
import include.filtering.test.RegExMatchingComponent;
import include.filtering.test.WantedBean;
import org.junit.jupiter.api.Test;

import java.io.Serializable;
import java.util.AbstractList;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ComponentScanFiltersIT {


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

    @Test
    public void shouldBeAbleToExcludeComponentsViaComponentScanningWithAnnotationExcludeFilter() throws Exception {
        try (DefaultBeanFactory beanFactory = new DefaultBeanFactory()) {
            beanFactory.register(ComponentScanWithAnnotationExcludeFilterConfig.class);
            beanFactory.refresh();

            assertFalse(beanFactory.contains(ExcludedComponent.class));
            assertThrows(NoSuchBeanDefinitionException.class, () -> beanFactory.getBean(ExcludedComponent.class));
        }
    }

    @Test
    public void shouldBeAbleToExcludeComponentsViaComponentScanningWithAssignableTypeExcludeFilter() throws Exception {
        try (DefaultBeanFactory beanFactory = new DefaultBeanFactory()) {
            beanFactory.register(ComponentScanWithAssignableTypeExcludeFilterConfig.class);
            beanFactory.refresh();

            assertFalse(beanFactory.contains(IterableComponent.class));
            assertTrue(beanFactory.contains(ExcludedComponent.class));
            assertThrows(NoSuchBeanDefinitionException.class, () -> beanFactory.getBean(IterableComponent.class));
        }
    }

    @Test
    public void shouldBeAbleToExcludeComponentsViaComponentScanningCombiningExcludeFilters() {
        DefaultBeanFactory beanFactory = new DefaultBeanFactory();
        beanFactory.register(ComponentScanWithCombinedExcludeFilterConfig.class);
        beanFactory.refresh();

        assertFalse(beanFactory.contains(IterableComponent.class));
        assertFalse(beanFactory.contains(ExcludedComponent.class));
        assertThrows(NoSuchBeanDefinitionException.class, () -> beanFactory.getBean(IterableComponent.class));
        assertThrows(NoSuchBeanDefinitionException.class, () -> beanFactory.getBean(ExcludedComponent.class));
    }


    @Configuration
    @ComponentScan(basePackages = "include.filtering.test",
            includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = WantedBean.class))
    public static class ComponentScanningFilteringConfig {
    }

    @Configuration
    @ComponentScan(basePackages = "include.filtering.test",
            includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {Serializable.class, AbstractList.class}))
    public static class ComponentScanningAssignableTypeFilteringConfig {
    }

    @Configuration
    @ComponentScan(basePackages = "include.filtering.test",
            includeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern = ".*RegEx.*"))
    public static class ComponentScanningRegExFilteringConfig {
    }

    @Configuration
    @ComponentScan(basePackages = "include.filtering.test",
            includeFilters = @ComponentScan.Filter(type = FilterType.CUSTOM, classes = CustomFilterType.class))
    public static class ComponentScanningWithCustomFilterConfig {
    }

    @Configuration
    @ComponentScan(basePackages = "exclude.filtering.test",
            excludeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = UnwantedBean.class))
    public static class ComponentScanWithAnnotationExcludeFilterConfig {
    }

    @Configuration
    @ComponentScan(basePackages = "exclude.filtering.test",
            excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = IterableComponent.class))
    public static class ComponentScanWithAssignableTypeExcludeFilterConfig {
    }

    @Configuration
    @ComponentScan(basePackages = "exclude.filtering.test",
            excludeFilters = {
                    @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = IterableComponent.class),
                    @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = UnwantedBean.class),
            })
    public static class ComponentScanWithCombinedExcludeFilterConfig {
    }

    private static class CustomFilterType implements TypeFilter {

        @Override
        public boolean match(MetadataReader metadataReader) {
            return metadataReader.getClassMetadata().getClassName().contains("FilteredComponent");
        }
    }
}
