package com.example.demo.model;

import jakarta.persistence.*;
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
    //Set<Lessons> lessons
    @ManyToOne
//    @JoinColumn(name = "teacher_id")
    private Teacher teacher;

    @Override
    public String toString() {
        return name + " " + surname;
    }
}
// Student id imie nazwisko jezyk