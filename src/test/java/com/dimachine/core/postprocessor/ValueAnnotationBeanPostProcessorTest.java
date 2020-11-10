package com.dimachine.core.postprocessor;

import com.dimachine.core.annotation.Value;
import com.dimachine.core.env.ConfigurableEnvironment;
import com.dimachine.core.env.Environment;
import com.dimachine.core.env.MapPropertySources;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ValueAnnotationBeanPostProcessorTest {

    @Test
    public void shouldBeAbleToInjectValuesIntoStringFields() {
        Environment environment = new ConfigurableEnvironment();
        environment.merge(new MapPropertySources(Map.of("application.name", "di-machine")));
        ValueAnnotationBeanPostProcessor beanPostProcessor = new ValueAnnotationBeanPostProcessor(environment);
        ValueBean bean = new ValueBean();

        beanPostProcessor.postProcessBeforeInitialisation(bean, "valueBean");

        assertEquals("di-machine", bean.applicationName);
    }

    @Test
    public void whenValuePlaceholderCannotBeResolved_shouldInjectThePlaceholder() {
        Environment environment = new ConfigurableEnvironment();
        ValueAnnotationBeanPostProcessor beanPostProcessor = new ValueAnnotationBeanPostProcessor(environment);
        ValueBean bean = new ValueBean();

        beanPostProcessor.postProcessBeforeInitialisation(bean, "valueBean");

        assertEquals("${application.name}", bean.applicationName);
    }

    private static class ValueBean {
        @Value("${application.name}")
        private String applicationName;
    }
}
