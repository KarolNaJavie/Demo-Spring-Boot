package com.example.demo.repository;

import com.example.demo.model.Lesson;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
public class LessonRepository {
    private final List<Lesson> lessons = new ArrayList<>();
    private int nextID = 1;
    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;

    public LessonRepository(TeacherRepository teacherRepository, StudentRepository studentRepository) {
        this.teacherRepository = teacherRepository;
        this.studentRepository = studentRepository;
    }

    @PostConstruct
    void init() {
        Lesson lesson1 = new Lesson(nextID, studentRepository.findAll().get(1), teacherRepository.findAll().get(1), LocalDate.EPOCH);
        nextID++;
        lessons.add(lesson1);
        Lesson lesson2 = new Lesson(nextID, studentRepository.findAll().get(0), teacherRepository.findAll().get(0), LocalDate.of(2026, 2, 12));
        nextID++;
        lessons.add(lesson2);
        Lesson lesson3 = new Lesson(nextID, studentRepository.findAll().get(2), teacherRepository.findAll().get(2), LocalDate.of(2026, 2, 13));
        nextID++;
        lessons.add(lesson3);
    }

    public List<Lesson> findAll() {
        return lessons;
    }

    public void deleteByID(int id) {
        lessons.removeIf(n -> n.getId() == id);
    }
}
