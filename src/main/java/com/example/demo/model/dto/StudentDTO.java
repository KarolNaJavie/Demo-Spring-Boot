package com.example.demo.model.dto;

import com.example.demo.model.Language;
import com.example.demo.model.Student;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StudentDto {

    private long id;
    private String firstName;
    private String lastName;
    private Language language;

    public static StudentDto fromEntity(Student student) {
        return StudentDto.builder()
                .id(student.getId())
                .firstName(student.getFirstName())
                .lastName(student.getLastName())
                .language(student.getLanguage())
                .build();
    }
}
