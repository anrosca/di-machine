package com.dimachine.core.test;

import com.dimachine.core.annotation.Component;

@Component("funky")
public class TestBean {
    private final FooService fooService;

    public TestBean(FooService fooService) {
        this.fooService = fooService;
    }

    public void greet() {
        fooService.foo();
    }
}
