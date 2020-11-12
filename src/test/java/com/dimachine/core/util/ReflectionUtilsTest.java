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
        TestDummy instance = new TestDummy();

        Field field = ReflectionUtils.getField(TestDummy.class, "name");
        ReflectionUtils.setField(instance, field, "<newValue>");

        assertEquals("<newValue>", instance.name);
    }

    @Test
    public void shouldBeAbleToInvokeMethods() {
        TestDummy instance = new TestDummy();

        Method method = ReflectionUtils.getMethod(instance.getClass(), "getName");
        String methodResult = (String) ReflectionUtils.invokeMethod(instance, method);

        assertEquals("<defaultValue>", methodResult);
    }

    @Test
    public void shouldBeAbleToGetAllDeclaredMethods() {

        List<String> expectedDeclaredMethodsNames = ReflectionUtils.getDeclaredMethods(TestDummy.class)
                .stream()
                .map(Method::getName)
                .collect(Collectors.toList());

        assertEquals(Set.of(
                "equals", "hashCode", "toString", "finalize",
                "wait", "notify", "notifyAll", "getClass", "clone",
                "privateMethod", "protectedMethod", "packageDefaultMethod", "init", "getName"
        ), new HashSet<>(expectedDeclaredMethodsNames));
    }

    @Test
    public void shouldWrapInRuntimeException_whenMethodCannotBeFound() {
        TestDummy instance = new TestDummy();

        assertThrows(RuntimeException.class, () -> ReflectionUtils.getMethod(instance.getClass(), "destroy"));
    }

    @Test
    public void shouldWrapInRuntimeException_whenFieldCannotBeFound() {
        TestDummy instance = new TestDummy();

        assertThrows(RuntimeException.class, () -> ReflectionUtils.getField(instance.getClass(), "init"));
    }

    @Test
    public void shouldWrapInRuntimeException_whenMethodInvocationFails() {
        TestDummy instance = new TestDummy();
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
                Arguments.of("public", TestDummy.class.getDeclaredMethod("getName").getModifiers()),
                Arguments.of("private", TestDummy.class.getDeclaredMethod("privateMethod").getModifiers()),
                Arguments.of("protected", TestDummy.class.getDeclaredMethod("protectedMethod").getModifiers()),
                Arguments.of("", TestDummy.class.getDeclaredMethod("packageDefaultMethod").getModifiers())
        );
    }

    @MethodSource("makeModifiersTestParameters")
    @ParameterizedTest
    public void shouldBeAbleToMakePrettyPublicModifier(String expectedModifier, int sourceModifier) {
        assertEquals(expectedModifier, ReflectionUtils.makePrettyModifiers(sourceModifier));
    }

    @Test
    public void shouldBeAbleToGetAllFields() throws Exception {
        Field[] declaredFields = ReflectionUtils.getDeclaredFields(TestDummy.class);

        assertArrayEquals(new Field[] {TestDummy.class.getDeclaredField("name")}, declaredFields);
    }

    @Test
    public void shouldBeAbleToMakeInstanceUsingNonDefaultConstructor() {
        TestDummy instance = ReflectionUtils.makeInstance(TestDummy.class, "ctor");

        assertEquals("ctor", instance.name);
    }

    @Test
    public void shouldThrowRuntimeException_whenNoMatchingConstructorWasFound() {
        assertThrows(RuntimeException.class, () -> ReflectionUtils.makeInstance(TestDummy.class, new Object[] {"a", "b"}));
    }

    private static class TestDummy {
        private String name = "<defaultValue>";

        public TestDummy() {
        }

        public TestDummy(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void init() {
            throw new IllegalArgumentException();
        }

        private void privateMethod() {
        }

        protected void protectedMethod() {
        }

        void packageDefaultMethod() {
        }
    }
}
