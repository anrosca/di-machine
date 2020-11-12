package com.dimachine.core.env;

import com.dimachine.core.BeanFactory;
import com.dimachine.core.annotation.Value;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Annotation;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class EnvironmentUtilTest {

    @Test
    public void shouldReturnFalse_whenPassingInAnEmptyArray() {
        Annotation[] annotations = {};

        assertFalse(EnvironmentUtil.isEnvironmentValue(annotations));
    }

    @Test
    public void shouldReturnTrue_whenValueAnnotationIsPresentInAnnotationArray() throws NoSuchMethodException {
        Annotation[][] annotations = TestDummy.class.getDeclaredConstructor(String.class).getParameterAnnotations();

        assertTrue(EnvironmentUtil.isEnvironmentValue(annotations[0]));
    }

    @Test
    public void shouldReturnNull_whenEvaluatingAnEmptyArray() {
        Annotation[] annotations = {};
        BeanFactory beanFactory = mock(BeanFactory.class);

        assertNull(EnvironmentUtil.resolveValue(annotations, beanFactory));
        verifyNoInteractions(beanFactory);
    }

    @Test
    public void shouldBeAbleToResolveEnvironmentValue() throws NoSuchMethodException {
        Annotation[][] annotations = TestDummy.class.getDeclaredConstructor(String.class).getParameterAnnotations();
        BeanFactory beanFactory = mock(BeanFactory.class);
        MapPropertySources propertySources = new MapPropertySources(Map.of("server.port", "8080"));
        when(beanFactory.getBean(Environment.class)).thenReturn(new ConfigurableEnvironment(propertySources));

        String resolvedValue = EnvironmentUtil.resolveValue(annotations[0], beanFactory);

        assertEquals("8080", resolvedValue);
    }

    private static class TestDummy {
        public TestDummy(@Value("${server.port}") String value) {
        }
    }
}
