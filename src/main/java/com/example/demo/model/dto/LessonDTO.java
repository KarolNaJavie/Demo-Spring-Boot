package com.example.demo.model.dto;

import com.example.demo.model.Lesson;
import com.example.demo.model.Student;
import com.example.demo.model.Teacher;
import lombok.Builder;
import lombok.Data;


import java.time.LocalDateTime;
@Data
@Builder
public class LessonDTO {

    private LocalDateTime datetime;
    private Long teacherId;
    private Long studentId;

    public static LessonDTO fromEntity(Lesson lesson){
        return LessonDTO.builder()
                .datetime(lesson.getDatetime())
                .teacherId(lesson.getTeacher().getId())
                .studentId(lesson.getStudent().getId())
                .build();
    }
}
