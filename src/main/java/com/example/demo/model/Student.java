package com.example.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Student {
    @Id
            @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
    String name;
    String surname;
//    Language language;

    @Override
    public String toString() {
        return name + " " + surname;
    }
}
// Student id imie nazwisko jezyk