package com.dimachine.core.integration;

import _async.AsyncBean;
import com.dimachine.core.BeanFactory;
import com.dimachine.core.DefaultBeanFactory;
import com.dimachine.core.annotation.Bean;
import com.dimachine.core.annotation.Configuration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import test.FooService;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class AsynchronousBeansIT {

    @BeforeEach
    void setUp() {
        AsyncBean.reset();
    }

    @Test
    public void shouldBeAbleToRunBeanMethodsAsynchronously() throws InterruptedException {
        BeanFactory beanFactory = new DefaultBeanFactory(AppConfig.class);
        beanFactory.refresh();

        AsyncBean bean = beanFactory.getBean(AsyncBean.class);
        bean.async();
        TimeUnit.MILLISECONDS.sleep(30);

        assertThatAsynchronousMethodWasCalled();
    }

    @Test
    public void shouldBeAbleToConstructAsyncBeansWhichHaveConstructorDependencies() throws InterruptedException {
        BeanFactory beanFactory = new DefaultBeanFactory(FooService.class.getPackageName(), AsyncBean.class.getPackageName());
        beanFactory.refresh();

        AsyncBean bean = beanFactory.getBean(AsyncBean.class);
        bean.async();
        TimeUnit.MILLISECONDS.sleep(30);

        assertThatAsynchronousMethodWasCalled();
    }

    private void assertThatAsynchronousMethodWasCalled() {
        String asyncMethodInvocations = String.join(",", AsyncBean.getAsyncMethodInvocations());
        String[] parts = asyncMethodInvocations.split("\\|");
        assertEquals(2, parts.length);
        assertNotEquals(Thread.currentThread().getName(), parts[0]);
        assertEquals("async", parts[1]);
    }

    @Configuration
    public static class AppConfig {
        @Bean
        public AsyncBean asyncBean() {
            return new AsyncBean();
        }
    }
}
