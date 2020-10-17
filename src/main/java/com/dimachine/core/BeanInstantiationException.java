package com.dimachine.core;

public class BeanInstantiationException extends RuntimeException {
    public BeanInstantiationException(String message, Exception cause) {
        super(message, cause);
    }
}
