package com.example.demo.model.dto;

import com.example.demo.model.Language;
import com.example.demo.model.Student;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class StudentDTO {
    private long id;
    private String name;
    private String surname;
    private Language language;

    public static StudentDTO fromEntity(Student student) {
        return StudentDTO.builder()
                .id(student.getId())
                .name(student.getName())
                .surname(student.getSurname())
                .language(student.getLanguage())
                .build();
    }
}
