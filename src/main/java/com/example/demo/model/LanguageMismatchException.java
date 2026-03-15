package com.example.demo.model;

public class LanguageMismatchException extends RuntimeException {
    public LanguageMismatchException() {
        super("Language does not match teachers language set");
    }
}
