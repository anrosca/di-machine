package com.dimachine.core.scanner;

import com.dimachine.core.BeanDefinition;
import com.dimachine.core.BeanScope;
import com.dimachine.core.SimpleBeanDefinition;
import com.dimachine.core.annotation.Bean;
import com.dimachine.core.annotation.Configuration;
import com.dimachine.core.annotation.Scope;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AnnotationBeanDefinitionScannerTest {

    @Test
    public void shouldBeAbleToScanBeanDefinitionsFromConfigurationClasses() {
        AnnotationBeanDefinitionScanner beanDefinitionScanner = new AnnotationBeanDefinitionScanner();

        List<BeanDefinition> beanDefinitions = beanDefinitionScanner.scanBeanDefinitionsFrom(AppConfig.class);

        assertEquals(Set.of(
                SimpleBeanDefinition.builder().scope(BeanScope.SINGLETON).beanName("myBean").build(),
                SimpleBeanDefinition.builder().scope(BeanScope.PROTOTYPE).beanName("yetAnotherMyBean").build(),
                SimpleBeanDefinition.builder().scope(BeanScope.SINGLETON).beanName("cool_bean").build()
        ), new HashSet<>(beanDefinitions));
    }

    @Configuration
    public static class AppConfig {
        @Bean
        public MyBean myBean() {
            return new MyBean();
        }

        @Scope(BeanScope.PROTOTYPE)
        @Bean(name = "yetAnotherMyBean")
        public MyBean anotherMyBean() {
            return new MyBean();
        }

        @Scope(BeanScope.SINGLETON)
        @Bean(name = "cool_bean")
        public MyBean coolBean() {
            return new MyBean();
        }

        @Bean
        private MyBean privateBean() {
            return new MyBean();
        }
    }

    private static class MyBean {
    }
}
