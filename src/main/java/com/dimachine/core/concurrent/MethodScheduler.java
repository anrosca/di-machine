package com.dimachine.core.concurrent;

import java.util.concurrent.ScheduledExecutorService;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class MethodScheduler {

    public void schedule(Runnable command, ScheduledExecutorService executorService, SchedulingProperties schedulingProperties) {
        long fixedRate = schedulingProperties.getFixedRate();
        long initialDelay = schedulingProperties.getInitialDelay();
        long fixedDelay = schedulingProperties.getFixedDelay();
        if (initialDelay > 0 && fixedRate > 0) {
            executorService.scheduleAtFixedRate(command, initialDelay, fixedRate, MILLISECONDS);
        } else if (initialDelay > 0 && fixedDelay > 0) {
            executorService.scheduleWithFixedDelay(command, initialDelay, fixedDelay, MILLISECONDS);
        } else {
            executorService.schedule(command, initialDelay, MILLISECONDS);
        }
    }
}
