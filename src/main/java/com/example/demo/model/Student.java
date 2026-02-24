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

    // -------------------------------------------------------------------------
    // @Enumerated(EnumType.STRING)
    //   Mówi Hibernate żeby zapisywał nazwę enuma (np. "JAVA") zamiast liczby (0).
    //   Domyślnie JPA zapisuje ORDINAL (0, 1, 2...) — to jest pułapka, bo gdy
    //   zmienisz kolejność wartości w enumie, stare rekordy w bazie zmienią sens.
    //   Zawsze używaj EnumType.STRING — kod jest czytelniejszy i bezpieczniejszy.
    // -------------------------------------------------------------------------
    @Enumerated(EnumType.STRING)
    private Language language;

    // -------------------------------------------------------------------------
    // @ManyToOne – wielu studentów należy do jednego nauczyciela
    //
    //   To jest strona WŁAŚCICIEL (owning side) relacji — tu Hibernate patrzy,
    //   żeby wiedzieć co zapisać do bazy danych.
    //
    // @JoinColumn(name = "teacher_id")
    //   Mówi JPA jak ma się nazywać kolumna klucza obcego w tabeli "student".
    //   Bez tej adnotacji Hibernate sam wygeneruje nazwę (zwykle "teacher_id"),
    //   ale dobrą praktyką jest nazwać ją jawnie — kod jest wtedy czytelniejszy.
    //
    //   DLACZEGO @JoinColumn jest tutaj, a nie w Teacher?
    //   Bo klucz obcy fizycznie siedzi w tabeli STUDENT (po stronie "wiele").
    //   Teacher nie ma żadnej dodatkowej kolumny — on tylko "patrzy" na studentów
    //   przez mappedBy.
    // -------------------------------------------------------------------------
    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;

    @Override
    public String toString() {
        return name + " " + surname;
    }
}