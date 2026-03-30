package com.example.demo.model.common.exception;

public class LessonCannotBeInThePastException extends RuntimeException {
    public LessonCannotBeInThePastException() {
        super("Lesson cannot be in the past");
    }
}
