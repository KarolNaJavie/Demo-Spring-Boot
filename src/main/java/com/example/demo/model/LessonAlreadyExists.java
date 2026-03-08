package com.example.demo.model;

public class LessonAlreadyExists extends RuntimeException {
    public LessonAlreadyExists(String message) {
        super(message);
    }
}
