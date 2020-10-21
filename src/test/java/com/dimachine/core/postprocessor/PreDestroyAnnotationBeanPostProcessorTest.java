package com.dimachine.core.postprocessor;

import com.dimachine.core.annotation.PreDestroy;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class PreDestroyAnnotationBeanPostProcessorTest {

    @Test
    public void shouldInvokeMethodsAnnotationWithPreDestroyUponClosing() {
        PreDestroyAnnotationBeanPostProcessor beanPostProcessor = new PreDestroyAnnotationBeanPostProcessor();
        AnnotatedDisposableBean bean = new AnnotatedDisposableBean();
        beanPostProcessor.postProcessBeforeInitialisation(bean, "disposableBean");

        beanPostProcessor.destroy();

        assertTrue(bean.wasDestroyed);
    }

    @Test
    public void shouldContinueDisposingBeans_evenIfPreDestroyMethodThrowsException() {
        PreDestroyAnnotationBeanPostProcessor beanPostProcessor = new PreDestroyAnnotationBeanPostProcessor();
        ExceptionThrowingDisposableBean bean = new ExceptionThrowingDisposableBean();
        beanPostProcessor.postProcessBeforeInitialisation(bean, "disposableBean");

        beanPostProcessor.destroy();

        assertEquals(Set.of("destroyExceptionally", "destroy"), new HashSet<>(bean.invokedMethods));
    }

    @Test
    public void shouldThrowInvalidDestroyMethodException_whenPreDestroyMethodHasParameters() {
        PreDestroyAnnotationBeanPostProcessor beanPostProcessor = new PreDestroyAnnotationBeanPostProcessor();
        InvalidDisposableBean bean = new InvalidDisposableBean();
        beanPostProcessor.postProcessBeforeInitialisation(bean, "disposableBean");

        assertThrows(InvalidDestroyMethodException.class, beanPostProcessor::destroy);
    }

    private static class AnnotatedDisposableBean {
        boolean wasDestroyed;

        @PreDestroy
        private void destroy() {
            wasDestroyed = true;
        }
    }

    private static class ExceptionThrowingDisposableBean {
        List<String> invokedMethods = new ArrayList<>();

        @PreDestroy
        private void destroyExceptionally() {
            invokedMethods.add("destroyExceptionally");
            throw new RuntimeException("Error while destroying bean");
        }

        @PreDestroy
        private void destroy() {
            invokedMethods.add("destroy");
        }
    }

    private static class InvalidDisposableBean {

        @PreDestroy
        private void destroy(int i) {
        }
    }
}
