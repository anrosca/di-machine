package com.dimachine.core.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

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
                "equals", "hashCode", "toString", "finalize",
                "wait", "notify", "notifyAll", "getClass", "clone",
                "f", "g", "h", "init", "getName"
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

    @Test
    public void shouldBeAbleToInstantiateClassUsingNoArgsConstructor() {
        Object instance = ReflectionUtils.makeInstance(ArrayList.class);

        assertNotNull(instance);
        assertEquals(instance.getClass(), ArrayList.class);
    }

    @Test
    public void shouldWrapInRuntimeException_whenInstanceCreationFails() {
        assertThrows(RuntimeException.class, () -> ReflectionUtils.makeInstance(AbstractList.class));
    }

    static Stream<Arguments> makeModifiersTestParameters() throws NoSuchMethodException {
        return Stream.of(
                Arguments.of("public", TestObject.class.getDeclaredMethod("getName").getModifiers()),
                Arguments.of("private", TestObject.class.getDeclaredMethod("f").getModifiers()),
                Arguments.of("protected", TestObject.class.getDeclaredMethod("g").getModifiers()),
                Arguments.of("", TestObject.class.getDeclaredMethod("h").getModifiers())
        );
    }

    @MethodSource("makeModifiersTestParameters")
    @ParameterizedTest
    public void shouldBeAbleToMakePrettyPublicModifier(String expectedModifier, int sourceModifier) {
        assertEquals(expectedModifier, ReflectionUtils.makePrettyModifiers(sourceModifier));
    }

    @Test
    public void shouldBeAbleToGetAllFields() throws Exception {
        Field[] declaredFields = ReflectionUtils.getDeclaredFields(TestObject.class);

        assertArrayEquals(new Field[] {TestObject.class.getDeclaredField("name")}, declaredFields);
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

        protected void g() {
        }

        void h() {
        }
    }
}
