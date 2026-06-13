package com.example.demo.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
public class CreateLessonCommand {
    private LocalDateTime datetime;
    private Long teacherId;
    private Long studentId;
}
