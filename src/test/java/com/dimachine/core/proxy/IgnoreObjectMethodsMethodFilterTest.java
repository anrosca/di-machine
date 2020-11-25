package com.dimachine.core.proxy;

import com.dimachine.core.util.ReflectionUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.reflect.Method;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class IgnoreObjectMethodsMethodFilterTest {

    static Stream<Arguments> makeTestParameters() {
        return Stream.of(
                Arguments.of("equals", new Class<?>[] {Object.class}),
                Arguments.of("hashCode", new Class<?>[] {}),
                Arguments.of("wait", new Class<?>[] {}),
                Arguments.of("wait", new Class<?>[] {long.class}),
                Arguments.of("wait", new Class<?>[] {long.class, int.class}),
                Arguments.of("notify", new Class<?>[] {}),
                Arguments.of("notifyAll", new Class<?>[] {}),
                Arguments.of("getClass", new Class<?>[] {}),
                Arguments.of("clone", new Class<?>[] {}),
                Arguments.of("finalize", new Class<?>[] {})
        );
    }

    @MethodSource("makeTestParameters")
    @ParameterizedTest
    public void shouldNotHandleObjectMethods(String methodName, Class<?>[] parameterTypes) {
        IgnoreObjectMethodsMethodFilter filter = new IgnoreObjectMethodsMethodFilter();
        Method method = ReflectionUtils.getMethod(getClass(), methodName, parameterTypes);

        assertFalse(filter.isHandled(method));
    }

    @Test
    public void shouldHandleNonObjectMethods() {
        IgnoreObjectMethodsMethodFilter filter = new IgnoreObjectMethodsMethodFilter();
        Method method = ReflectionUtils.getMethod(getClass(), "regularMethod");

        assertTrue(filter.isHandled(method));
    }

    private static void regularMethod() {
    }
}
