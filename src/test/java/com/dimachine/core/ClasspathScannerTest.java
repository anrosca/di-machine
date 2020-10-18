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
        List<String> foundBeanClassed = scanner.scan();

        assertEquals(List.of(
                TestComponentWithExplicitName.class.getName(),
                TestComponentWithoutExplicitName.class.getName()
        ), foundBeanClassed);
    }

    @Test
    public void shouldFindServiceBeanDefinitionsFromGivenPackage() {
        ClasspathScanner scanner = new ClasspathScanner("com.dimachine.core._service");
        List<String> foundBeanClasses = scanner.scan();

        assertEquals(List.of(
                TestServiceWithExplicitName.class.getName(),
                TestServiceWithoutExplicitName.class.getName()
        ), foundBeanClasses);
    }
}
