package com.dimachine.core.integration;

import com.dimachine.core.BeanFactory;
import com.dimachine.core.DefaultBeanFactory;
import com.dimachine.core.annotation.Bean;
import com.dimachine.core.annotation.Component;
import com.dimachine.core.annotation.Configuration;
import com.dimachine.core.annotation.Scheduled;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ScheduledAnnotationIT {
    private static List<String> invocations;

    @BeforeEach
    public void setUp() {
        invocations = new CopyOnWriteArrayList<>();
    }

    @AfterEach
    public void tearDown() {
        invocations = new CopyOnWriteArrayList<>();
    }

    @Test
    public void shouldBeAbleToScheduleMethodsWithInitialDelay() throws Exception {
        BeanFactory beanFactory = new DefaultBeanFactory(AppConfig.class);
        beanFactory.refresh();
        TimeUnit.MILLISECONDS.sleep(15);
        beanFactory.close();

        assertEquals("scheduled()", String.join(";", invocations));
    }

    @Configuration
    public static class AppConfig {

        @Bean
        public ScheduledService scheduledService() {
            return new ScheduledService();
        }
    }

    @Component
    public static class ScheduledService {

        @Scheduled(initialDelay = 5)
        public void scheduled() {
            invocations.add("scheduled()");
        }
    }
}
