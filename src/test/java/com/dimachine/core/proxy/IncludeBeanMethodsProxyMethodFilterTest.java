package com.dimachine.core.proxy;

import com.dimachine.core.annotation.Bean;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

public class IncludeBeanMethodsProxyMethodFilterTest {

    @Test
    public void shouldHandleMethodsAnnotatedWithBean() throws Exception {
        IncludeBeanMethodsProxyMethodFilter filter = new IncludeBeanMethodsProxyMethodFilter();

        Method method = getClass().getDeclaredMethod("beanMethod");
        assertTrue(filter.isHandled(method));
    }

    @Test
    public void shouldIgnoreMethodsWhichHaveNoBeanAnnotation() throws Exception {
        IncludeBeanMethodsProxyMethodFilter filter = new IncludeBeanMethodsProxyMethodFilter();

        Method method = getClass().getDeclaredMethod("regularMethod");
        assertFalse(filter.isHandled(method));
    }

    @Bean
    private static void beanMethod() {
    }

    private static void regularMethod() {
    }
}
