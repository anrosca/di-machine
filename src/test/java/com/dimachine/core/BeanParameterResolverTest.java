package com.dimachine.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class BeanParameterResolverTest {

    private final BeanFactory beanFactory = mock(BeanFactory.class);
    private final BeanParameterResolver parameterResolver = new BeanParameterResolver(beanFactory);
    private final SomeService resolvedParameter = new SomeService();

    @BeforeEach
    public void setUp() {
        when(beanFactory.getBean(SomeService.class)).thenReturn(resolvedParameter);
    }

    @Test
    public void shouldBeAbleToResolvePlainFieldValue() throws NoSuchFieldException {
        Field field = FieldsConfigClass.class.getDeclaredField("someService");

        Object resolvedField = parameterResolver.resolve(new FieldsConfigClass(), field);

        assertEquals(SomeService.class, resolvedField.getClass());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldBeAbleToResolveListFieldValue() throws NoSuchFieldException {
        List<SomeService> resolvedValue = List.of(new SomeService());
        when(beanFactory.getAllBeansOfType(SomeService.class)).thenReturn(resolvedValue);
        Field field = FieldsConfigClass.class.getDeclaredField("someServiceList");

        List<SomeService> actualResolvedFieldValue = (List<SomeService>) parameterResolver.resolve(new FieldsConfigClass(), field);

        assertEquals(resolvedValue, actualResolvedFieldValue);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldBeAbleToResolveListWithWildcardsFieldValue() throws NoSuchFieldException {
        List<SomeService> resolvedValue = List.of(new SomeService());
        when(beanFactory.getAllBeansOfType(SomeService.class)).thenReturn(resolvedValue);
        Field field = FieldsConfigClass.class.getDeclaredField("someServiceWildcardList");

        List<SomeService> actualResolvedFieldValue = (List<SomeService>) parameterResolver.resolve(new FieldsConfigClass(), field);

        assertEquals(resolvedValue, actualResolvedFieldValue);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldBeAbleToResolveSetFieldValue() throws NoSuchFieldException {
        Set<SomeService> resolvedValue = Set.of(new SomeService());
        when(beanFactory.getAllBeansOfType(SomeService.class)).thenReturn(List.copyOf(resolvedValue));
        Field field = FieldsConfigClass.class.getDeclaredField("someServiceSet");

        Set<SomeService> actualResolvedFieldValue = (Set<SomeService>) parameterResolver.resolve(new FieldsConfigClass(), field);

        assertEquals(resolvedValue, actualResolvedFieldValue);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldBeAbleToResolveWildcardSetFieldValue() throws NoSuchFieldException {
        Set<SomeService> resolvedValue = Set.of(new SomeService());
        when(beanFactory.getAllBeansOfType(SomeService.class)).thenReturn(List.copyOf(resolvedValue));
        Field field = FieldsConfigClass.class.getDeclaredField("someServiceWildcardSet");

        Set<SomeService> actualResolvedFieldValue = (Set<SomeService>) parameterResolver.resolve(new FieldsConfigClass(), field);

        assertEquals(resolvedValue, actualResolvedFieldValue);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldBeAbleToResolveWildcardMapFieldValue() throws NoSuchFieldException {
        Map<String, SomeService> resolvedValue = Map.of("someService", new SomeService());
        when(beanFactory.getBeansMapOfType(SomeService.class)).thenReturn(resolvedValue);
        Field field = FieldsConfigClass.class.getDeclaredField("someServiceWildcardMap");

        Map<String, SomeService> actualResolvedFieldValue = (Map<String, SomeService>) parameterResolver.resolve(new FieldsConfigClass(), field);

        assertEquals(resolvedValue, actualResolvedFieldValue);
    }

    @Test
    public void whenMethodHasNoParameters_shouldReturnEmptyParameterArray() throws NoSuchMethodException {
        Method method = MethodsConfigClass.class.getMethod("noParameters");

        Object[] parameters = parameterResolver.resolve(method);

        assertEquals(0, parameters.length);
    }

    @Test
    public void shouldBeAbleToResolveSingleMethodParameter() throws NoSuchMethodException {
        Method method = MethodsConfigClass.class.getMethod("singleParameter", SomeService.class);

        Object[] parameters = parameterResolver.resolve(method);

        assertArrayEquals(new Object[]{resolvedParameter}, parameters);
        verify(beanFactory).getBean(SomeService.class);
    }

    @Test
    public void shouldBeAbleToResolveMultipleMethodParameter() throws NoSuchMethodException {
        YetAnotherService anotherResolvedParameter = new YetAnotherService();
        when(beanFactory.getBean(YetAnotherService.class)).thenReturn(anotherResolvedParameter);
        Method method = MethodsConfigClass.class.getMethod("twoParameters", SomeService.class, YetAnotherService.class);

        Object[] parameters = parameterResolver.resolve(method);

        assertArrayEquals(new Object[]{resolvedParameter, anotherResolvedParameter}, parameters);
        verify(beanFactory).getBean(SomeService.class);
        verify(beanFactory).getBean(YetAnotherService.class);
    }

    @Test
    public void shouldBeAbleToResolveListMethodParameterType() throws NoSuchMethodException {
        when(beanFactory.getAllBeansOfType(SomeService.class)).thenReturn(List.of(resolvedParameter));
        Method method = MethodsConfigClass.class.getMethod("listParameter", List.class);

        Object[] parameters = parameterResolver.resolve(method);

        assertArrayEquals(new Object[]{List.of(resolvedParameter)}, parameters);
        verify(beanFactory).getAllBeansOfType(SomeService.class);
    }

    @Test
    public void shouldBeAbleToResolveListWithWildcardMethodParameterType() throws NoSuchMethodException {
        when(beanFactory.getAllBeansOfType(SomeService.class)).thenReturn(List.of(resolvedParameter));
        Method method = MethodsConfigClass.class.getMethod("listWildcardParameter", List.class);

        Object[] parameters = parameterResolver.resolve(method);

        assertArrayEquals(new Object[]{List.of(resolvedParameter)}, parameters);
        verify(beanFactory).getAllBeansOfType(SomeService.class);
    }

    @Test
    public void shouldBeAbleToResolveSetMethodParameterType() throws NoSuchMethodException {
        when(beanFactory.getAllBeansOfType(SomeService.class)).thenReturn(List.of(resolvedParameter));
        Method method = MethodsConfigClass.class.getMethod("setParameter", Set.class);

        Object[] parameters = parameterResolver.resolve(method);

        assertArrayEquals(new Object[]{Set.of(resolvedParameter)}, parameters);
        verify(beanFactory).getAllBeansOfType(SomeService.class);
    }

    @Test
    public void shouldBeAbleToResolveMapMethodParameterType() throws NoSuchMethodException {
        when(beanFactory.getBeansMapOfType(SomeService.class)).thenReturn(Map.of("someService", resolvedParameter));
        Method method = MethodsConfigClass.class.getMethod("mapParameter", Map.class);

        Object[] parameters = parameterResolver.resolve(method);

        assertArrayEquals(new Object[]{Map.of("someService", resolvedParameter)}, parameters);
        verify(beanFactory).getBeansMapOfType(SomeService.class);
    }

    @Test
    public void shouldBeAbleToResolveMapWithWildcardsMethodParameterType() throws NoSuchMethodException {
        when(beanFactory.getBeansMapOfType(SomeService.class)).thenReturn(Map.of("someService", resolvedParameter));
        Method method = MethodsConfigClass.class.getMethod("mapWithWildcardsParameter", Map.class);

        Object[] parameters = parameterResolver.resolve(method);

        assertArrayEquals(new Object[]{Map.of("someService", resolvedParameter)}, parameters);
        verify(beanFactory).getBeansMapOfType(SomeService.class);
    }

    @Test
    public void shouldThrowBeanInitialisationException_whenResolvingMapWithNonStringKeyMethodParameterType() throws NoSuchMethodException {
        when(beanFactory.getBeansMapOfType(SomeService.class)).thenReturn(Map.of("someService", resolvedParameter));
        Method method = MethodsConfigClass.class.getMethod("mapWrongKeyTypeParameter", Map.class);

        assertThrows(BeanInitializationException.class, () -> parameterResolver.resolve(method));
    }

    private static class MethodsConfigClass {
        public Object noParameters() {
            return new Object();
        }

        public Object singleParameter(SomeService someService) {
            return new Object();
        }

        public Object twoParameters(SomeService someService, YetAnotherService anotherService) {
            return new Object();
        }

        public Object listParameter(List<SomeService> someServices) {
            return new Object();
        }

        public Object listWildcardParameter(List<? extends SomeService> someServices) {
            return new Object();
        }

        public Object setParameter(Set<SomeService> someServices) {
            return new Object();
        }

        public Object mapParameter(Map<String, SomeService> someServices) {
            return new Object();
        }

        public Object mapWithWildcardsParameter(Map<String, ? extends SomeService> someServices) {
            return new Object();
        }

        public Object mapWrongKeyTypeParameter(Map<YetAnotherService, ? extends SomeService> someServices) {
            return new Object();
        }
    }

    private static class FieldsConfigClass {
        private SomeService someService;

        private List<SomeService> someServiceList;

        private Set<SomeService> someServiceSet;

        private List<? super SomeService> someServiceWildcardList;

        private Set<? super SomeService> someServiceWildcardSet;

        private Map<String, ? super SomeService> someServiceWildcardMap;
    }

    private static class SomeService {
    }

    private static class YetAnotherService {
    }
}
