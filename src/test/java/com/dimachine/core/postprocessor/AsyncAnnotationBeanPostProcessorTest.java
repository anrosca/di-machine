package com.dimachine.core.postprocessor;

import com.dimachine.core.DefaultBeanFactory;
import com.dimachine.core.annotation.Async;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AsyncAnnotationBeanPostProcessorTest {
    private static volatile List<String> asyncMethodInvocations;
    private static final String ASYNC_THREAD_NAME = "async-thread";

    private final ExecutorService executorService = mock(ExecutorService.class);
    private final DefaultBeanFactory beanFactory = mock(DefaultBeanFactory.class);
    private final AsyncAnnotationBeanPostProcessor beanPostProcessor =
            new AsyncAnnotationBeanPostProcessor(beanFactory, executorService);

    @BeforeEach
    public void setUp() {
        asyncMethodInvocations = new CopyOnWriteArrayList<>();
        doAnswer(invocationOnMock -> {
            Runnable runnable = invocationOnMock.getArgument(0);
            Thread thread = new Thread(runnable);
            thread.setName(ASYNC_THREAD_NAME);
            thread.start();
            thread.join();
            return null;
        }).when(executorService).execute(any(Runnable.class));
    }

    @Test
    public void shouldThrowInvalidAsyncMethodException_whenMethodReturnTypeIsNotVoid() {
        assertThrows(InvalidAsyncMethodException.class,
                () -> beanPostProcessor.postProcessBeforeInitialisation(new InvalidAsyncBean(), "asyncBean"));
    }

    @Test
    public void whenNoAsyncMethodsArePresent_shouldNotProxyTheBean() {
        RegularDummyBean bean = new RegularDummyBean();

        beanPostProcessor.postProcessBeforeInitialisation(bean, "syncBean");
        RegularDummyBean actualBean = (RegularDummyBean) beanPostProcessor.postProcessAfterInitialisation(bean, "syncBean");

        assertEquals(RegularDummyBean.class, actualBean.getClass());
    }

    @Test
    public void asynchronousMethodShouldBeProxiedAndRunOnDifferentThread() throws InterruptedException {
        AsyncBean asyncBean = new AsyncBean();

        beanPostProcessor.postProcessBeforeInitialisation(asyncBean, "asyncBean");
        AsyncBean actualBean = (AsyncBean) beanPostProcessor.postProcessAfterInitialisation(asyncBean, "asyncBean");
        actualBean.async();
        TimeUnit.MILLISECONDS.sleep(15);

        assertNotEquals(AsyncBean.class, actualBean.getClass());
        assertEquals(ASYNC_THREAD_NAME + "|async", String.join(",", asyncMethodInvocations));
    }

    @Test
    public void methodNotAnnotatedWithAsync_ShouldBeCalledSynchronously() {
        AsyncBean asyncBean = new AsyncBean();

        beanPostProcessor.postProcessBeforeInitialisation(asyncBean, "asyncBean");
        AsyncBean actualBean = (AsyncBean) beanPostProcessor.postProcessAfterInitialisation(asyncBean, "asyncBean");
        actualBean.sync();

        assertNotEquals(AsyncBean.class, actualBean.getClass());
        String currentThreadName = Thread.currentThread().getName();
        assertEquals(currentThreadName + "|sync", String.join(",", asyncMethodInvocations));
    }

    @Test
    public void shouldTerminateExecutorService_uponBeanDisposal() throws InterruptedException {
        beanPostProcessor.destroy();

        verify(executorService).shutdown();
        verify(executorService).awaitTermination(1, TimeUnit.MINUTES);
        verify(executorService).shutdownNow();
    }

    @Test
    public void whenExecutorServiceTerminatesNormally_shouldNotForceShutdown() throws InterruptedException {
        when(executorService.awaitTermination(1, TimeUnit.MINUTES)).thenReturn(true);

        beanPostProcessor.destroy();

        verify(executorService).shutdown();
        verify(executorService).awaitTermination(1, TimeUnit.MINUTES);
        verify(executorService, never()).shutdownNow();
    }

    private static class InvalidAsyncBean {
        @Async
        public String invalid() {
            return "";
        }
    }

    public static class AsyncBean {
        @Async
        public void async() {
            asyncMethodInvocations.add(Thread.currentThread().getName() + "|async");
        }

        public void sync() {
            asyncMethodInvocations.add(Thread.currentThread().getName() + "|sync");
        }
    }

    private static class RegularDummyBean {
        public void sync() {
            asyncMethodInvocations.add(Thread.currentThread().getName() + "|async");
        }
    }
}
