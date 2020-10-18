package com.dimachine.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SimpleBeanDefinitionTest {

    @Test
    public void shouldBeAbleToGetBeanClass() {
        SimpleBeanDefinition beanDefinition = SimpleBeanDefinition.builder()
                .className(SimpleBeanDefinition.class.getName())
                .beanName("test")
                .build();

        assertEquals(SimpleBeanDefinition.class, beanDefinition.getBeanClass());
        assertEquals(SimpleBeanDefinition.class.getName(), beanDefinition.getClassName());
        assertEquals(BeanScope.SINGLETON, beanDefinition.getBeanScope());
        assertEquals("test", beanDefinition.getBeanName());
        assertTrue(beanDefinition.isSingleton());
        assertFalse(beanDefinition.isPrototype());
    }

    @Test
    public void whenBeanClassNameIsWrong_shouldThrowBeanClassMissingException() {
        SimpleBeanDefinition beanDefinition = SimpleBeanDefinition.builder()
                .className("<yay>")
                .beanName("test")
                .build();

        assertThrows(BeanClassMissingException.class, beanDefinition::getBeanClass);
    }
}
