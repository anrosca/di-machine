package com.dimachine.core;

import com.dimachine.core._component.TestComponentWithExplicitName;
import com.dimachine.core._component.TestComponentWithoutExplicitName;
import com.dimachine.core._service.TestServiceWithExplicitName;
import com.dimachine.core._service.TestServiceWithoutExplicitName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class ClasspathScannerTest {

    @Test
    public void shouldFindComponentBeanDefinitionsFromGivenPackage() {
        ClasspathScanner scanner = new ClasspathScanner("com.dimachine.core._component");
        Set<BeanDefinition> foundBeanDefinitions = scanner.scan();

        assertEquals(Set.of(
                new SimpleBeanDefinition(TestComponentWithExplicitName.class.getName(), "explicitComponent"),
                new SimpleBeanDefinition(TestComponentWithoutExplicitName.class.getName(), "testComponentWithoutExplicitName")
        ), foundBeanDefinitions);
    }

    @Test
    public void shouldFindServiceBeanDefinitionsFromGivenPackage() {
        ClasspathScanner scanner = new ClasspathScanner("com.dimachine.core._service");
        Set<BeanDefinition> foundBeanDefinitions = scanner.scan();

        assertEquals(Set.of(
                new SimpleBeanDefinition(TestServiceWithExplicitName.class.getName(), "explicitService"),
                new SimpleBeanDefinition(TestServiceWithoutExplicitName.class.getName(), "testServiceWithoutExplicitName")
        ), foundBeanDefinitions);
    }
}
