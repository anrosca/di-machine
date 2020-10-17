package com.dimachine.core.test;

import com.dimachine.core.annotation.Component;

@Component("funky")
public class TestBean {
    public void greet() {
        System.out.println("Hello, world!");
    }
}
