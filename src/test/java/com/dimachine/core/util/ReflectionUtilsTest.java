package com.dimachine.core.util;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ReflectionUtilsTest {

    @Test
    public void shouldBeAbleToSetFieldValues() {
        TestObject instance = new TestObject();

        Field field = ReflectionUtils.getField(TestObject.class, "name");
        ReflectionUtils.setField(instance, field, "<newValue>");

        assertEquals("<newValue>", instance.name);
    }

    @Test
    public void shouldBeAbleToInvokeMethods() {
        TestObject instance = new TestObject();

        Method method = ReflectionUtils.getMethod(instance.getClass(), "getName");
        String methodResult = (String) ReflectionUtils.invokeMethod(instance, method);

        assertEquals("<defaultValue>", methodResult);
    }

    @Test
    public void shouldBeAbleToGetAllDeclaredMethods() {

        List<String> expectedDeclaredMethodsNames = ReflectionUtils.getDeclaredMethods(TestObject.class)
                .stream()
                .map(Method::getName)
                .collect(Collectors.toList());

        assertEquals(Set.of(
                "equals", "hashCode", "toString", "finalize", "wait", "notify", "notifyAll", "getClass", "clone",
                "f", "init", "getName"
        ), new HashSet<>(expectedDeclaredMethodsNames));
    }

    @Test
    public void shouldWrapInRuntimeException_whenMethodCannotBeFound() {
        TestObject instance = new TestObject();

        assertThrows(RuntimeException.class, () -> ReflectionUtils.getMethod(instance.getClass(), "destroy"));
    }

    @Test
    public void shouldWrapInRuntimeException_whenFieldCannotBeFound() {
        TestObject instance = new TestObject();

        assertThrows(RuntimeException.class, () -> ReflectionUtils.getField(instance.getClass(), "init"));
    }

    @Test
    public void shouldWrapInRuntimeException_whenMethodInvocationFails() {
        TestObject instance = new TestObject();
        Method method = ReflectionUtils.getMethod(instance.getClass(), "init");

        assertThrows(RuntimeException.class, () -> ReflectionUtils.invokeMethod(instance, method));
    }

    private static class TestObject {
        private String name = "<defaultValue>";

        public String getName() {
            return name;
        }

        public void init() {
            throw new IllegalArgumentException();
        }

        private void f() {
        }
    }
}
