package com.dimachine.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DefaultBeanNamerTest {

    @Test
    public void shouldMakeBeanNameWithFirstLetterLowercased_fromBeanClass() {
        DefaultBeanNamer beanNamer = new DefaultBeanNamer();

        assertEquals("beanNamer", beanNamer.makeBeanName(BeanNamer.class.getName()));
    }
}
