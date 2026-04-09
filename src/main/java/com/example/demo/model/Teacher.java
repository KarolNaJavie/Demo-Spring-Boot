package com.example.demo.model;

import com.example.demo.model.common.Language;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Teacher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Long version;

    private String firstName;
    private String lastName;

    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "teacher_language", joinColumns = @JoinColumn(name = "teacher_id"))
    @Column(name = "language")
    private Set<Language> languages;

    @OneToMany(mappedBy = "teacher")
    private Set<Student> students;

    @OneToMany(mappedBy = "teacher")
    private Set<Lesson> lessons;

    @Override
    public String toString() {
        return firstName + " " + lastName;
    }
}
