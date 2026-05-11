package com.example.demo.model.dto;

import com.example.demo.model.Lesson;
import com.example.demo.model.Student;
import com.example.demo.model.Teacher;
import lombok.Builder;
import lombok.Data;


import java.time.LocalDateTime;

@Builder
public class LessonDTO {

    private LocalDateTime datetime;
    private Teacher teacher;
    private Student student;

    public static LessonDTO fromEntity(Lesson lesson){
        return LessonDTO.builder()
                .datetime(lesson.getDatetime())
                .teacher(lesson.getTeacher())
                .student(lesson.getStudent())
                .build();
    }
}
