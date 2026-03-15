package com.example.demo.model;

public class TermUnavailableException extends RuntimeException {
    public TermUnavailableException() {
        super("This date is not available");
    }
}
