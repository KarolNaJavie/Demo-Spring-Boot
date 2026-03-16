package com.example.demo.model.dto;

import com.example.demo.model.Language;
import com.example.demo.model.Teacher;
import lombok.Builder;
import lombok.Getter;

import java.util.Set;

@Getter
@Builder
public class TeacherDTO {

    private long id;
    private String firstName;
    private String lastName;
    private Set<Language> languages;

    public static TeacherDTO fromEntity(Teacher teacher) {
        return TeacherDTO.builder()
                .id(teacher.getId())
                .firstName(teacher.getFirstName())
                .lastName(teacher.getLastName())
                .languages(teacher.getLanguages())
                .build();
    }
}
