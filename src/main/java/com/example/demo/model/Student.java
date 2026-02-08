package com.example.demo.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class Student {
    int id;
    String name;
    String surname;
    Language language;
}
// Student id imie nazwisko jezyk