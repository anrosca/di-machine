package com.dimachine.core.test;

import com.dimachine.core.annotation.Service;

@Service
public class FooService {
    public void foo() {
        System.out.println("Hello, from FooService!");
    }
}
