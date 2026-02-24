package com.example.demo.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Lesson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private LocalDateTime termin;

    // =========================================================================
    // TWOJE ZADANIE
    // =========================================================================
    // Lekcja (Lesson) ma dwóch uczestników: studenta i nauczyciela.
    // Wzorując się na relacji Teacher <-> Student, którą właśnie omówiliśmy,
    // dodaj tutaj DWUKIERUNKOWĄ relację:
    //
    //   1. Lesson -> Student   (wiele lekcji należy do jednego studenta)
    //   2. Lesson -> Teacher   (wiele lekcji należy do jednego nauczyciela)
    //
    // Pamiętaj o:
    //   - właściwych adnotacjach po stronie Lesson (strona "wiele")
    //   - @JoinColumn z czytelną nazwą kolumny
    //   - uzupełnieniu drugiej strony relacji w klasach Student i Teacher
    //     (strona "jeden" — tak jak Set<Student> w Teacher)
    //   - mappedBy po stronie odwrotnej wskazuje na pole w tej klasie
    // =========================================================================
}