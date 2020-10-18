package com.dimachine.core;

public class BeanClassMissingException extends RuntimeException {
    public BeanClassMissingException(String message, Exception cause) {
        super(message, cause);
    }
}
