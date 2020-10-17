package com.dimachine.core;

public class BeanCurrentlyInCreationException extends RuntimeException {
    public BeanCurrentlyInCreationException(String message) {
        super(message);
    }
}
