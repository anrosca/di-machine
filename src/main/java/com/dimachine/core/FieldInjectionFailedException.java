package com.dimachine.core;

public class FieldInjectionFailedException extends RuntimeException {
    public FieldInjectionFailedException(String message, Exception cause) {
        super(message, cause);
    }
}
