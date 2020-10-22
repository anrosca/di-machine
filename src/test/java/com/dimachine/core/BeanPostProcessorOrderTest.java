package com.dimachine.core;

import com.dimachine.core.annotation.Ordered;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class BeanPostProcessorOrderTest {
    private static List<String> beansPostProcessorInvocationOrder = new ArrayList<>();

    private DefaultBeanFactory beanFactory;
    private final SimpleBeanDefinition beanDefinition = SimpleBeanDefinition.builder()
            .className(TargetBean.class.getName())
            .beanName("testBean")
            .build();

    @BeforeEach
    public void setUp() {
        beanFactory = new DefaultBeanFactory(new String[] {}) {
            @Override
            protected List<String> scanClasspath(List<String> additionalPackages) {
                return Collections.emptyList();
            }
        };
    }

    @AfterEach
    void tearDown() {
        beansPostProcessorInvocationOrder = new ArrayList<>();
    }

    @Test
    public void shouldInvokeBeanPostProcessorsAfterSingletonInstantiation() {
        SimpleBeanDefinition mediumBPP = SimpleBeanDefinition.builder()
                .className(MediumOrderBeanPostProcessor.class.getName())
                .beanName("mediumOrderBeanPostProcessor")
                .build();
        SimpleBeanDefinition lowestBPP = SimpleBeanDefinition.builder()
                .className(LowestOrderBeanPostProcessor.class.getName())
                .beanName("lowestOrderBeanPostProcessor")
                .build();
        SimpleBeanDefinition highestBPP = SimpleBeanDefinition.builder()
                .className(HighestOrderBeanPostProcessor.class.getName())
                .beanName("highestOrderBeanPostProcessor")
                .build();
        beanFactory.registerBeans(highestBPP, lowestBPP, mediumBPP, beanDefinition);

        beanFactory.refresh();

        TargetBean bean = beanFactory.getBean(TargetBean.class);
        assertNotNull(bean);
        assertEquals(bean.getClass(), TargetBean.class);
        assertEquals(List.of("high()", "medium()", "low()"), beansPostProcessorInvocationOrder);
    }

    @Ordered(Order.HIGHEST_PRECEDENCE)
    static class HighestOrderBeanPostProcessor implements BeanPostProcessor {
        @Override
        public Object postProcessBeforeInitialisation(Object bean, String beanName) {
            beansPostProcessorInvocationOrder.add("high()");
            return bean;
        }
    }

    @Ordered(Order.LEAST_PRECEDENCE)
    static class LowestOrderBeanPostProcessor implements BeanPostProcessor {
        @Override
        public Object postProcessBeforeInitialisation(Object bean, String beanName) {
            beansPostProcessorInvocationOrder.add("low()");
            return bean;
        }
    }

    static class MediumOrderBeanPostProcessor implements BeanPostProcessor {
        @Override
        public Object postProcessBeforeInitialisation(Object bean, String beanName) {
            beansPostProcessorInvocationOrder.add("medium()");
            return bean;
        }
    }

    private static class TargetBean implements Comparable<TargetBean> {
        public TargetBean() {
        }

        @Override
        public int compareTo(TargetBean other) {
            return 0;
        }
    }
}
