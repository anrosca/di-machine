package com.dimachine.core.postprocessor;

public class InvalidAsyncMethodException extends RuntimeException {
    public InvalidAsyncMethodException(String message) {
        super(message);
    }
}
