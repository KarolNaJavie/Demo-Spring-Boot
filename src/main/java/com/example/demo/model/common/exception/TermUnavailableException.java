package com.example.demo.model.common.exception;

public class TermUnavailableException extends RuntimeException {
    public TermUnavailableException() {
        super("This date is not available");
    }
}
