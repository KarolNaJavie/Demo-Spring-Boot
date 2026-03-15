package com.example.demo.model;

public class LessonHasAlreadyStartedException extends RuntimeException {
    public LessonHasAlreadyStartedException() {
        super("Lesson has already started");
    }
}
