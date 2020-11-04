package com.dimachine.core.concurrent;

import com.dimachine.core.postprocessor.SchedulingProperties;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MethodScheduler {

    public void schedule(Runnable runnable, ScheduledExecutorService executorService, SchedulingProperties schedulingProperties) {
        executorService.schedule(runnable, schedulingProperties.getInitialDelay(), TimeUnit.MILLISECONDS);
    }
}
