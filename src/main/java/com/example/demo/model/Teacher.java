package com.example.demo.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
// =============================================================================
// RELACJA Teacher <-> Student  (OneToMany / ManyToOne)
// =============================================================================
// W bazie danych wygląda to tak:
//
//   Tabela: teacher          Tabela: student
//   +---------+             +------------+------------+
//   | id | .. |             | id | ... | teacher_id  |
//   +---------+             +------------+------------+
//   |  1 |   |              |  1 |     |      1      |  <-- Anna uczy się u Teacher(1)
//   |  2 |   |              |  2 |     |      1      |  <-- Bartek też u Teacher(1)
//   +---------+             |  3 |     |      2      |  <-- Celina u Teacher(2)
//                           +------------+------------+
//
// Kolumna "teacher_id" w tabeli student to tzw. KLUCZ OBCY (foreign key).
// To ona "trzyma" całą relację w bazie — jest po stronie WIELU (Many).
// =============================================================================
public class Teacher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String surname;

    // -------------------------------------------------------------------------
    // @Enumerated(EnumType.STRING)
    //   Mówi Hibernate żeby zapisywał nazwę enuma (np. "JAVA") zamiast liczby (0).
    //   Domyślnie JPA zapisuje ORDINAL (0, 1, 2...) — to jest pułapka, bo gdy
    //   zmienisz kolejność wartości w enumie, stare rekordy w bazie zmienią sens.
    //   Zawsze używaj EnumType.STRING — kod jest czytelniejszy i bezpieczniejszy.
    // -------------------------------------------------------------------------
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "teacher_language", joinColumns = @JoinColumn(name = "teacher_id"))
    @Column(name = "language")  //language z kolekcji w bazie
    @Enumerated(EnumType.STRING)
    private Set<Language> languages;

    // -------------------------------------------------------------------------
    // @OneToMany – jeden nauczyciel ma wielu studentów
    //
    // mappedBy = "teacher"
    //   Mówi JPA: "NIE twórz tutaj żadnej nowej kolumny ani tabeli pomocniczej.
    //   Relacja jest już zmapowana przez pole 'teacher' w klasie Student."
    //
    //   Bez mappedBy Hibernate stworzyłby dodatkową tabelę pośrednią
    //   (np. teacher_students), co jest zbędne przy relacji OneToMany/ManyToOne.
    //
    //   Strona z mappedBy = strona ODWROTNA (inverse) — ona tylko "czyta" relację.
    //   Strona BEZ mappedBy = strona WŁAŚCICIEL (owning) — ona zapisuje do bazy.
    // -------------------------------------------------------------------------
    @OneToMany(mappedBy = "teacher")
    private Set<Student> students;

    @OneToMany(mappedBy = "teacher")
    private Set<Lesson> lessons;

    @Override
    public String toString() {
        return name + " " + surname;
    }
}
