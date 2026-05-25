package com.example.demo.model.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Value;

import java.util.ArrayList;
import java.util.List;

@Getter

public class ValidationExceptionDTO extends ExceptionDTO {
    private final List<ViolationInfo> violations = new ArrayList<>();

    public ValidationExceptionDTO() {
        super("Validation Failed");
    }

    public void addViolation(String field, String message){
        violations.add(new ViolationInfo(field, message));
    }

    @Getter
    @AllArgsConstructor
    private static class ViolationInfo {
        private String field;
        private String message;
    }
}
