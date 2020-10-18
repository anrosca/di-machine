package com.dimachine.core;

import com.dimachine.core.test.TestBean;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class BeanFactoryIT {
    @Test
    public void shouldBeAbleToScanPackageAndGetBeans() {
        DefaultBeanFactory beanFactory = new DefaultBeanFactory("com.dimachine.core.test");
        beanFactory.refresh();
        TestBean bean = beanFactory.getBean(TestBean.class);

        assertTrue(bean.initMethodWasCalled());
    }
}
