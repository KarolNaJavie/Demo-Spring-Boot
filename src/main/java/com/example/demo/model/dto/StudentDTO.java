package com.example.demo.model.dto;

import com.example.demo.model.Language;
import com.example.demo.model.Student;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StudentDTO {

    private long id;
    private String firstName;
    private String lastName;
    private Language language;

    public static StudentDTO fromEntity(Student student) {
        return StudentDTO.builder()
                .id(student.getId())
                .firstName(student.getFirstName())
                .lastName(student.getLastName())
                .language(student.getLanguage())
                .build();
    }
}
