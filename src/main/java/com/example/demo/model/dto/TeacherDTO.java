package com.example.demo.model.dto;

import com.example.demo.model.Language;
import com.example.demo.model.Teacher;
import lombok.Builder;
import lombok.Getter;

import java.util.Set;

@Builder
@Getter
public class TeacherDTO {
    private  long id;
    private String name;
    private String surname;
    private Set<Language> languages;

    public static TeacherDTO fromEntity(Teacher teacher){
    return TeacherDTO.builder()
            .id(teacher.getId())
            .name(teacher.getName())
            .surname(teacher.getSurname())
            .languages(teacher.getLanguages())
            .build();
    }}
