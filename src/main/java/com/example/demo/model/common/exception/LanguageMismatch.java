package com.example.demo.model.common.exception;

public class LanguageMismatch extends RuntimeException {
    public LanguageMismatch() {
        super("Languages of teacher and student dont match!");
    }
}
