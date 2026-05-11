package com.example.demo.model;

import com.example.demo.model.common.Language;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Set;
@Data
public class CreateTeacherCommand {
    @NotNull(message = "NULL_VALUE")
    private String firstName;
    @NotNull(message = "NULL_VALUE")
    private String lastName;
    @NotEmpty(message = "EMPTY_VALUE")
    private Set<Language> languages;

    public Teacher toEntity() {
        return Teacher.builder()
                .firstName(firstName)
                .lastName(lastName)
                .languages(languages)
                .build();
    }
}
