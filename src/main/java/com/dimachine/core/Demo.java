package com.dimachine.core;

import com.dimachine.core.test.TestBean;

public class Demo {
    public static void main(String[] args) {
        DefaultBeanFactory beanFactory = new DefaultBeanFactory("com.dimachine.core.test");
        beanFactory.refresh();
        TestBean bean = beanFactory.getBean(TestBean.class);
    }
}
