package com.example.demo.model;

public class LessonCannotBeInThePastException extends RuntimeException {
    public LessonCannotBeInThePastException() {
        super("Lesson cannot be in the past");
    }
}
