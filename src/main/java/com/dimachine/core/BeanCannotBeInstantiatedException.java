package com.dimachine.core;

public class BeanCannotBeInstantiatedException extends RuntimeException {
    public BeanCannotBeInstantiatedException(String message) {
        super(message);
    }
}
