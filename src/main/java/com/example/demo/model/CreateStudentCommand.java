package com.example.demo.model;

import com.example.demo.model.common.Language;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

import java.util.Set;
@Builder
@Data
public class CreateStudentCommand {
    @NotNull(message = "NULL_VALUE")
    private String firstName;
    @NotNull(message = "NULL_VALUE")
    private String lastName;
    @NotEmpty(message = "EMPTY_VALUE")
    private Language language;
    @Positive(message = "NEGATIVE_VALUE")
    private long teacherId;

    public Student toEntity() {
        return Student.builder()
                .firstName(firstName)
                .lastName(lastName)
                .language(language)
                .build();
    }
}
