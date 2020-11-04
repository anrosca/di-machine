package com.dimachine.core.postprocessor;

import com.dimachine.core.BeanFactory;
import com.dimachine.core.annotation.Scheduled;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class ScheduledAnnotationBeanPostProcessorTest {
    private volatile boolean methodExecuted = false;

    private final ScheduledExecutorService executorService = mock(ScheduledExecutorService.class);
    private final BeanFactory beanFactory = mock(BeanFactory.class);
    private final ScheduledAnnotationBeanPostProcessor beanPostProcessor = new ScheduledAnnotationBeanPostProcessor(beanFactory, executorService);

    @BeforeEach
    public void setUp() {
        doAnswer(invocationOnMock -> {
            Runnable runnable = invocationOnMock.getArgument(0);
            runnable.run();
            return null;
        }).when(executorService).schedule(any(Runnable.class), anyLong(), any(TimeUnit.class));
    }

    @Test
    public void shouldBeAbleToScheduleMethodsWithInitialDelayOnly() {
        Object bean = new ScheduledBean();
        String beanName = "scheduledBean";
        when(beanFactory.getBean(beanName)).thenReturn(bean);

        beanPostProcessor.postProcessBeforeInitialisation(bean, beanName);
        beanPostProcessor.postProcessAfterInitialisation(bean, beanName);

        assertTrue(methodExecuted);
    }

    @Test
    public void beansThatHaveNoScheduledAnnotationShouldNotBeScheduled() {
        Object bean = new NonScheduledBean();
        String beanName = "scheduledBean";
        when(beanFactory.getBean(beanName)).thenReturn(bean);

        beanPostProcessor.postProcessBeforeInitialisation(bean, beanName);
        beanPostProcessor.postProcessAfterInitialisation(bean, beanName);

        assertFalse(methodExecuted);
        verifyNoInteractions(executorService);
    }

    @Test
    public void whenDisposingBeanPostProcessor_shouldShutdownExecutorService() throws InterruptedException {
        beanPostProcessor.destroy();

        verify(executorService).shutdown();
        verify(executorService).awaitTermination(anyLong(), any());
        verify(executorService).shutdownNow();
    }

    @Test
    public void whenDisposingBeanPostProcessorAndAwaitTerminationThrowsException_shouldShutdownExecutorServiceImmediately() throws InterruptedException {
        when(executorService.awaitTermination(anyLong(), any())).thenThrow(InterruptedException.class);

        beanPostProcessor.destroy();

        verify(executorService).shutdown();
        verify(executorService).awaitTermination(anyLong(), any());
        verify(executorService).shutdownNow();
    }

    @Test
    public void whenDisposingBeanPostProcessorAndAwaitTerminationReturnsGracefully_shouldStopDisposingBean() throws InterruptedException {
        when(executorService.awaitTermination(anyLong(), any())).thenReturn(true);

        beanPostProcessor.destroy();

        verify(executorService).shutdown();
        verify(executorService).awaitTermination(anyLong(), any());
        verify(executorService, never()).shutdownNow();
    }

    @Test
    public void shouldThrowInvalidScheduledMethodException_whenScheduledMethodHasParameters() {
        Object bean = new InvalidScheduledBean();
        String beanName = "scheduledBean";

        beanPostProcessor.postProcessBeforeInitialisation(bean, beanName);
        InvalidScheduledMethodException exception =
                assertThrows(InvalidScheduledMethodException.class, () -> beanPostProcessor.postProcessAfterInitialisation(bean, beanName));
        assertEquals("Invalid @Scheduled method InvalidScheduledBean.execute(String)." +
                " @Scheduled methods should have no parameters", exception.getMessage());
    }

    private class ScheduledBean {
        @Scheduled(initialDelay = 10)
        public void execute() {
            methodExecuted = true;
        }
    }

    private static class NonScheduledBean {
        public void execute() {
            fail("This method shouldn't have been executed");
        }
    }

    private static class InvalidScheduledBean {
        @Scheduled(initialDelay = 10)
        public void execute(String name) {
            fail("This method shouldn't have been executed");
        }
    }
}
