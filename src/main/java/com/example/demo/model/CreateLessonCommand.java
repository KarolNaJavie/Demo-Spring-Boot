package com.example.demo.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateLessonCommand {
    private LocalDateTime datetime;
    private Long teacherId;
    private Long studentId;

}
