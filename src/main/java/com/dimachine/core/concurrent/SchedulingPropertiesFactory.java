package com.dimachine.core.concurrent;

import com.dimachine.core.annotation.Scheduled;

import java.time.Duration;

public class SchedulingPropertiesFactory {

    public static SchedulingProperties from(Scheduled scheduled) {
        return SchedulingProperties.builder()
                .initialDelay(makeDelay(scheduled.initialDelay(), scheduled.initialDelayString()))
                .fixedDelay(makeDelay(scheduled.fixedDelay(), scheduled.fixedDelayString()))
                .fixedRate(makeDelay(scheduled.fixedRate(), scheduled.fixedRateString()))
                .build();
    }

    private static long makeDelay(long delay, String delayAsString) {
        return delayAsString.isEmpty() ? delay : Duration.parse(delayAsString).toMillis();
    }
}
