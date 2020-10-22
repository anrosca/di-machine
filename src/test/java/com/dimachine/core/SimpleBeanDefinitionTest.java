package com.dimachine.core;

import org.junit.jupiter.api.Test;

import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.*;

public class SimpleBeanDefinitionTest {

    @Test
    public void shouldBeAbleToGetBeanClass() {
        SimpleBeanDefinition beanDefinition = SimpleBeanDefinition.builder()
                .className(SimpleBeanDefinition.class.getName())
                .beanName("test")
                .build();

        assertEquals(SimpleBeanDefinition.class, beanDefinition.getRealBeanClass());
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

        assertThrows(BeanClassMissingException.class, beanDefinition::getRealBeanClass);
    }

    @Test
    public void beanDefinitionsWithTheSameState_shouldBeEqual() {
        SimpleBeanDefinition firstBeanDefinition = SimpleBeanDefinition.builder()
                .className(SimpleBeanDefinition.class.getName())
                .beanName("test")
                .build();
        SimpleBeanDefinition secondBeanDefinition = SimpleBeanDefinition.builder()
                .className(SimpleBeanDefinition.class.getName())
                .beanName("test")
                .build();

        assertEquals(secondBeanDefinition, firstBeanDefinition);
    }

    @Test
    public void beanDefinitionsWithDifferentBeanNames_shouldNotBeEqual() {
        SimpleBeanDefinition firstBeanDefinition = SimpleBeanDefinition.builder()
                .className(SimpleBeanDefinition.class.getName())
                .beanName("test")
                .build();
        SimpleBeanDefinition secondBeanDefinition = SimpleBeanDefinition.builder()
                .className(SimpleBeanDefinition.class.getName())
                .beanName("simpleBeanDefinition")
                .build();

        assertNotEquals(secondBeanDefinition, firstBeanDefinition);
    }

    @Test
    public void toStringShouldReturnHumanReadableString() {
        SimpleBeanDefinition beanDefinition = SimpleBeanDefinition.builder()
                .className(SimpleBeanDefinition.class.getName())
                .beanName("simpleBeanDefinition")
                .build();

        String expectedStringRepresentation = "SimpleBeanDefinition{className='com.dimachine.core.SimpleBeanDefinition', " +
                "beanName='simpleBeanDefinition', scope=SINGLETON}";
        assertEquals(expectedStringRepresentation, beanDefinition.toString());
    }

    @Test
    public void shouldBeAbleToCheckCompatibilityByNameAndType() {
        SimpleBeanDefinition beanDefinition = SimpleBeanDefinition.builder()
                .className(SimpleBeanDefinition.class.getName())
                .beanName("simpleBeanDefinition")
                .build();

        assertTrue(beanDefinition.isCompatibleWith("simpleBeanDefinition", BeanDefinition.class));
    }

    @Test
    public void shouldTellThatBeanDefinitionsAreIncompatible_whenTheirNamedAreNotEqual() {
        SimpleBeanDefinition beanDefinition = SimpleBeanDefinition.builder()
                .className(SimpleBeanDefinition.class.getName())
                .beanName("simpleBeanDefinition")
                .build();

        assertFalse(beanDefinition.isCompatibleWith("something", BeanDefinition.class));
    }

    @Test
    public void shouldTellThatBeanDefinitionsAreIncompatible_whenTheirTypesAreNotAssignable() {
        SimpleBeanDefinition beanDefinition = SimpleBeanDefinition.builder()
                .className(SimpleBeanDefinition.class.getName())
                .beanName("simpleBeanDefinition")
                .build();

        assertFalse(beanDefinition.isCompatibleWith("simpleBeanDefinition", Comparator.class));
    }
}
