package com.dimachine.core.concurrent;

import com.dimachine.core.BeanFactory;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MethodExecutingRunnableTest {
    private boolean methodExecuted = false;

    @Test
    public void shouldBeAbleToMakeRunnablesExecutingBeanMethods() throws Exception {
        Method method = MethodService.class.getMethod("execute");
        String beanName = "methodService";
        BeanFactory beanFactory = mock(BeanFactory.class);
        when(beanFactory.getBean(beanName)).thenReturn(new MethodService());
        MethodExecutingRunnable runnable = new MethodExecutingRunnable(method, beanName, beanFactory);

        runnable.run();

        assertTrue(methodExecuted);
    }

    private class MethodService {
        public void execute() {
           methodExecuted = true;
        }
    }
}
