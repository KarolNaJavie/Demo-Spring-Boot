package com.example.demo.model.common.exception;

public class LessonHasAlreadyStartedException extends RuntimeException {
    public LessonHasAlreadyStartedException() {
        super("Lesson has already started");
    }
}
