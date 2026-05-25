package com.example.demo.model.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ValidationExceptionDTO handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        ValidationExceptionDTO validationExceptionDTO = new ValidationExceptionDTO();
        exception.getFieldErrors().forEach(fieldError -> validationExceptionDTO.addViolation(fieldError.getField(), fieldError.getDefaultMessage()));
        return validationExceptionDTO;
    }

    @ExceptionHandler(LanguageMismatch.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionDTO handleLanguageMismatchException(LanguageMismatch exception) {
        return new ExceptionDTO(exception.getMessage());
    }

    @ExceptionHandler(LessonCannotBeInThePastException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionDTO handleLessonCannotBeInThePastEception(LessonCannotBeInThePastException exception) {
        return new ExceptionDTO(exception.getMessage());
    }

    @ExceptionHandler(LessonHasAlreadyStartedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionDTO handleLessonHasAlreadyStartedException(LessonHasAlreadyStartedException exception) {
        return new ExceptionDTO(exception.getMessage());
    }

    @ExceptionHandler(TermUnavailableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionDTO handleTermUnavailableException(TermUnavailableException exception) {
        return new ExceptionDTO(exception.getMessage());
    }
}
