package com.example.demo.service;

import com.example.demo.model.*;
import com.example.demo.repository.LessonRepository;
import com.example.demo.repository.StudentRepository;
import com.example.demo.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class LessonService {
    private final LessonRepository lessonRepository;
    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;

    public List<Lesson> findAll() {
        return lessonRepository.findAll();
    }

    public void deleteById(long id) {
        Lesson lesson = lessonRepository.findById(id).orElseThrow(() -> new LessonNotFoundException("Lesson not found: " + id));
        if (lesson.getDatetime().isAfter(LocalDateTime.now())) {
            lessonRepository.deleteById(id);
        }
    }

    public void save(Lesson lesson, Long teacherId, Long studentId) {
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new TeacherNotFoundException("Teacher not found: " + teacherId));
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new StudentNotFoundException("Student not found: " + studentId));

        LocalDateTime from = lesson.getDatetime().minusHours(1);
        LocalDateTime to = lesson.getDatetime().plusHours(1);

        if (lessonRepository.existsByTeacherAndDatetimeGreaterThanAndDatetimeLessThan(teacher, from, to)) {
            throw new LessonAlreadyExists("Lekcja juz istnieje!");
        }
        lesson.setTeacher(teacher);
        lesson.setStudent(student);
        lessonRepository.save(lesson);
    }
}
