package com.dimachine.core;

import com.dimachine.core.postprocessor.AutowiredAnnotationBeanPostProcessor;
import com.dimachine.core.postprocessor.PostConstructAnnotationBeanPostProcessor;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DiMachineFactoriesLoaderTest {

    @Test
    public void shouldBeAbleToReadFactoriesFile() {
        DiMachineFactoriesLoader factoriesLoader = new DiMachineFactoriesLoader();

        List<BeanDefinition> beanDefinitions = factoriesLoader.load();

        assertEquals(Set.of(
                SimpleBeanDefinition.builder()
                        .beanName("postConstructAnnotationBeanPostProcessor")
                        .className(PostConstructAnnotationBeanPostProcessor.class.getName())
                        .build(),
                SimpleBeanDefinition.builder()
                        .beanName("autowiredAnnotationBeanPostProcessor")
                        .className(AutowiredAnnotationBeanPostProcessor.class.getName())
                        .build()
        ), new HashSet<>(beanDefinitions));
    }
}