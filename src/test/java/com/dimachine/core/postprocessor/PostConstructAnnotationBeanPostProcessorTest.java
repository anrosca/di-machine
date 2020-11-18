package com.dimachine.core.postprocessor;

import com.dimachine.core.BeanInitializationException;
import com.dimachine.core.annotation.PostConstruct;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class PostConstructAnnotationBeanPostProcessorTest {

    private final PostConstructAnnotationBeanPostProcessor beanPostProcessor = new PostConstructAnnotationBeanPostProcessor();
    private final Set<String> invokedMethods = new HashSet<>();

    @Test
    public void shouldInvokeMethodsAnnotatedWithPostConstruct() {
        Object bean = new InitMethodTestBean();

        Object returnedInstance = beanPostProcessor.postProcessBeforeInitialisation(bean, "testBean");

        assertSame(returnedInstance, bean);
        assertEquals(Set.of("init()"), invokedMethods);
    }

    @Test
    public void shouldInvokeMethodsAnnotatedWithPostConstruct_evenFromSuperclasses() {
        Object bean = new SubclassInitMethodTestBean();

        Object returnedInstance = beanPostProcessor.postProcessBeforeInitialisation(bean, "testBean");

        assertSame(returnedInstance, bean);
        assertEquals(Set.of("init()", "postConstruct()"), invokedMethods);
    }

    @Test
    public void shouldThrowBeanInitialisationException_whenPostConstructMethodHasParameters() {
        Object bean = new IncorrectInitMethodTestBean();

        assertThrows(BeanInitializationException.class, () -> beanPostProcessor.postProcessBeforeInitialisation(bean, "testBean"));
    }

    @Test
    public void shouldThrowBeanInitialisationException_whenInitMethodThrowsException() {
        Object bean = new ExceptionInitMethodTestBean();

        assertThrows(BeanInitializationException.class, () -> beanPostProcessor.postProcessBeforeInitialisation(bean, "testBean"));
    }

    private class InitMethodTestBean {
        @PostConstruct
        public void init() {
            invokedMethods.add("init()");
        }
    }

    private class SubclassInitMethodTestBean extends InitMethodTestBean {
        @PostConstruct
        private void postConstruct() {
            invokedMethods.add("postConstruct()");
        }
    }

    private static class IncorrectInitMethodTestBean {
        @PostConstruct
        private void init(String value) {
        }
    }

    private static class ExceptionInitMethodTestBean {
        @PostConstruct
        private void init() {
            throw new IllegalStateException();
        }
    }
}
