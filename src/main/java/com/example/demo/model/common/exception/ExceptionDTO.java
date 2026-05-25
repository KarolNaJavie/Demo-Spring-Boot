package com.example.demo.model.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class ExceptionDTO  {
    private final LocalDateTime timeStamp = LocalDateTime.now();
    private final String message;

}
