package com.example.demo.model;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class Lesson {
    int id;
    Student student;
    Teacher teacher;
    LocalDate termin;
}
//Lesson (id, Kursant, Nauczyciel, termin)