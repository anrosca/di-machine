package com.dimachine.core.postprocessor;

import com.dimachine.core.annotation.Scheduled;
import com.dimachine.core.concurrent.SchedulingProperties;
import com.dimachine.core.concurrent.SchedulingPropertiesFactory;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.reflect.Method;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SchedulingPropertiesFactoryTest {

    public static Stream<Arguments> makeTestArguments() {
        return Stream.of(
                Arguments.of("primitiveInitialDelay", SchedulingProperties.builder().initialDelay(123L).build()),
                Arguments.of("initialDelayAsString", SchedulingProperties.builder().initialDelay(456L).build()),
                Arguments.of("primitiveFixedDelay", SchedulingProperties.builder().fixedDelay(1000L).build()),
                Arguments.of("fixedDelayAsString", SchedulingProperties.builder().fixedDelay(2000L).build()),
                Arguments.of("primitiveFixedRate", SchedulingProperties.builder().fixedRate(666).build()),
                Arguments.of("fixedRateString", SchedulingProperties.builder().fixedRate(60_000).build()),
                Arguments.of("fixedRateAsStringWithPrimitiveFixedDelay", SchedulingProperties.builder().fixedRate(60_000).initialDelay(123).build()),
                Arguments.of("primitiveFixedRateWithFixedDelayAsString", SchedulingProperties.builder().fixedRate(2000).initialDelay(123).build())
        );
    }

    @MethodSource("makeTestArguments")
    @ParameterizedTest
    public void shouldBeAbleToParseScheduledAnnotationProperties(String methodName, SchedulingProperties expectedProperties) throws Exception {
        Scheduled scheduled = getScheduledAnnotationFromMethod(methodName);

        SchedulingProperties actualProperties = SchedulingPropertiesFactory.from(scheduled);

        assertEquals(expectedProperties, actualProperties);
    }

    private Scheduled getScheduledAnnotationFromMethod(String methodName) throws Exception {
        Method method = getClass().getDeclaredMethod(methodName);
        return method.getAnnotation(Scheduled.class);
    }

    @Scheduled(initialDelay = 123)
    private static void primitiveInitialDelay() {
    }

    @Scheduled(initialDelayString = "PT0.456S")
    private static void initialDelayAsString() {
    }

    @Scheduled(fixedDelay = 1000)
    private static void primitiveFixedDelay() {
    }

    @Scheduled(fixedDelayString = "PT2S")
    private static void fixedDelayAsString() {
    }

    @Scheduled(fixedRate = 666)
    private static void primitiveFixedRate() {
    }

    @Scheduled(fixedRateString = "PT1M")
    private static void fixedRateString() {
    }

    @Scheduled(fixedRateString = "PT1M", initialDelay = 123)
    private static void fixedRateAsStringWithPrimitiveFixedDelay() {
    }

    @Scheduled(fixedRate = 2000, initialDelayString = "PT0.123S")
    private static void primitiveFixedRateWithFixedDelayAsString() {
    }
}
