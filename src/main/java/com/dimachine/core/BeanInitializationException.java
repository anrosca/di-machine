package com.dimachine.core;

public class BeanInitializationException extends RuntimeException {
    public BeanInitializationException(String message, Exception cause) {
        super(message, cause);
    }

    public BeanInitializationException(String message) {
        super(message);
    }
}
