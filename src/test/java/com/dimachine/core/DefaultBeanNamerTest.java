package com.dimachine.core;

import com.dimachine.core.annotation.Bean;
import com.dimachine.core.util.ReflectionUtils;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DefaultBeanNamerTest {

    @Test
    public void shouldMakeBeanNameWithFirstLetterLowercased_fromBeanClass() {
        DefaultBeanNamer beanNamer = new DefaultBeanNamer();

        assertEquals("beanNamer", beanNamer.makeBeanName(BeanNamer.class.getName()));
    }

    @Test
    public void beanNameShouldBeMethodName_whenBeanAnnotationHasNoParameters() {
        DefaultBeanNamer beanNamer = new DefaultBeanNamer();
        Method method = ReflectionUtils.getMethod(BeanMethods.class, "fooService");

        String actualBeanName = beanNamer.makeBeanName(method, method.getAnnotation(Bean.class));

        assertEquals("fooService", actualBeanName);
    }

    @Test
    public void whenBeanAnnotationHasParameters_beanNameShouldBeEqualToAnnotationParameter() {
        DefaultBeanNamer beanNamer = new DefaultBeanNamer();
        Method method = ReflectionUtils.getMethod(BeanMethods.class, "barService");

        String actualBeanName = beanNamer.makeBeanName(method, method.getAnnotation(Bean.class));

        assertEquals("BAR_SERVICE", actualBeanName);
    }

    @Test
    public void beanNameShouldDefaultToMethodName_whenMethodsHaveNoAnnotations() {
        DefaultBeanNamer beanNamer = new DefaultBeanNamer();
        Method method = ReflectionUtils.getMethod(BeanMethods.class, "bazService");

        String actualBeanName = beanNamer.makeBeanName(method, method.getAnnotation(Bean.class));

        assertEquals("bazService", actualBeanName);
    }

    private static class BeanMethods {
        @Bean
        public Object fooService() {
            return new Object();
        }

        @Bean(name = "BAR_SERVICE")
        public Object barService() {
            return new Object();
        }

        public Object bazService() {
            return new Object();
        }
    }
}
