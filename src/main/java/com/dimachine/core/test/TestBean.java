package com.dimachine.core.test;

import com.dimachine.core.annotation.Component;
import com.dimachine.core.annotation.PostConstruct;

@Component("funky")
public class TestBean {
    private final FooService fooService;

    public TestBean(FooService fooService) {
        this.fooService = fooService;
    }

    @PostConstruct
    public void greet() {
        fooService.foo();
    }
}
