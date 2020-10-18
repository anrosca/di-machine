package com.dimachine.core;

public class BeanInitialisationException extends RuntimeException {
    public BeanInitialisationException(String message, Exception cause) {
        super(message, cause);
    }

    public BeanInitialisationException(String message) {
        super(message);
    }
}
