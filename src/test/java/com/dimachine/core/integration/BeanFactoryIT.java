package com.dimachine.core.integration;

import com.dimachine.core.DefaultBeanFactory;
import test.TestBean;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class BeanFactoryIT {
    @Test
    public void shouldBeAbleToScanPackageAndGetBeans() {
        DefaultBeanFactory beanFactory = new DefaultBeanFactory("test");
        beanFactory.refresh();
        TestBean bean = beanFactory.getBean(TestBean.class);

        assertTrue(bean.initMethodWasCalled());
    }
}
