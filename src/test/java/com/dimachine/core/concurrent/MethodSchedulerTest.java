package com.dimachine.core.concurrent;

import org.junit.jupiter.api.Test;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class MethodSchedulerTest {

    private final MethodScheduler methodScheduler = new MethodScheduler();
    private final ScheduledExecutorService executorService = mock(ScheduledExecutorService.class);
    private final Runnable command = () -> {};

    @Test
    public void shouldBeAbleToScheduleRunnablesWithInitialDelayOnly() {
        SchedulingProperties schedulingProperties = SchedulingProperties.builder().initialDelay(100).build();

        methodScheduler.schedule(command, executorService, schedulingProperties);

        verify(executorService).schedule(command, 100, TimeUnit.MILLISECONDS);
    }

    @Test
    public void shouldBeAbleToScheduleRunnablesWithFixedDelay() {
        SchedulingProperties schedulingProperties = SchedulingProperties.builder().fixedDelay(123).initialDelay(100).build();

        methodScheduler.schedule(command, executorService, schedulingProperties);

        verify(executorService).scheduleWithFixedDelay(command, 100, 123, TimeUnit.MILLISECONDS);
    }

    @Test
    public void shouldBeAbleToScheduleRunnablesWithFixedRate() {
        SchedulingProperties schedulingProperties = SchedulingProperties.builder().fixedRate(456).initialDelay(100).build();

        methodScheduler.schedule(command, executorService, schedulingProperties);

        verify(executorService).scheduleAtFixedRate(command, 100, 456, TimeUnit.MILLISECONDS);
    }
}
