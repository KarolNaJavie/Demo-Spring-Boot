package com.example.demo.model;

import com.example.demo.model.common.Language;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateStudentCommand {
    @NotNull(message = "NULL_VALUE")
    private String firstName;
    @NotNull(message = "NULL_VALUE")
    private String lastName;
    @NotNull(message = "EMPTY_VALUE")
    private Language language;
    @NotNull(message = "NULL_VALUE")
    @Positive(message = "NEGATIVE_VALUE")
    private Long teacherId;

    public Student toEntity() {
        return Student.builder()
                .firstName(firstName)
                .lastName(lastName)
                .language(language)
                .build();
    }
}
