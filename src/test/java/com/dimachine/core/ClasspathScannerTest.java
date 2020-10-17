package com.dimachine.core;

import com.dimachine.core._component.TestComponentWithExplicitName;
import com.dimachine.core._component.TestComponentWithoutExplicitName;
import com.dimachine.core._service.TestServiceWithExplicitName;
import com.dimachine.core._service.TestServiceWithoutExplicitName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ClasspathScannerTest {

    @Test
    public void shouldFindComponentBeanDefinitionsFromGivenPackage() {
        ClasspathScanner scanner = new ClasspathScanner("com.dimachine.core._component");
        List<BeanDefinition> foundBeanDefinitions = scanner.scan();

        assertEquals(List.of(
                new SimpleBeanDefinition(TestComponentWithExplicitName.class.getName(), "explicitComponent"),
                new SimpleBeanDefinition(TestComponentWithoutExplicitName.class.getName(), "testComponentWithoutExplicitName")
        ), foundBeanDefinitions);
    }

    @Test
    public void shouldFindServiceBeanDefinitionsFromGivenPackage() {
        ClasspathScanner scanner = new ClasspathScanner("com.dimachine.core._service");
        List<BeanDefinition> foundBeanDefinitions = scanner.scan();

        assertEquals(List.of(
                new SimpleBeanDefinition(TestServiceWithExplicitName.class.getName(), "explicitService"),
                new SimpleBeanDefinition(TestServiceWithoutExplicitName.class.getName(), "testServiceWithoutExplicitName")
        ), foundBeanDefinitions);
    }
}
