package com.dimachine.core;

public enum Order {
    LEAST_PRECEDENCE(2_000_000_000),
    HIGHEST_PRECEDENCE(-2_000_000_000),
    DEFAULT_PRECEDENCE(1);

    private final int precedence;

    Order(int precedence) {
        this.precedence = precedence;
    }

    public int getPrecedence() {
        return precedence;
    }
}
