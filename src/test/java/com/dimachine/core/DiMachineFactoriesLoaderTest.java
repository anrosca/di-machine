package com.dimachine.core;

import com.dimachine.core.postprocessor.*;
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
                        .build(),
                SimpleBeanDefinition.builder()
                        .beanName("preDestroyAnnotationBeanPostProcessor")
                        .className(PreDestroyAnnotationBeanPostProcessor.class.getName())
                        .build(),
                SimpleBeanDefinition.builder()
                        .beanName("scheduledAnnotationBeanPostProcessor")
                        .className(ScheduledAnnotationBeanPostProcessor.class.getName())
                        .build(),
                SimpleBeanDefinition.builder()
                        .beanName("valueAnnotationBeanPostProcessor")
                        .className(ValueAnnotationBeanPostProcessor .class.getName())
                        .build(),
                SimpleBeanDefinition.builder()
                        .beanName("asyncAnnotationBeanPostProcessor")
                        .className(AsyncAnnotationBeanPostProcessor .class.getName())
                        .build()
        ), new HashSet<>(beanDefinitions));
    }
}
