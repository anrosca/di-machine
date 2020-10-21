package com.dimachine.core.postprocessor;

public class SetterInjectionFailedException extends RuntimeException {
    public SetterInjectionFailedException(String message, Exception cause) {
        super(message, cause);
    }
}
