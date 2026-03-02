package com.example.demo.service;

import com.example.demo.model.Lesson;
import com.example.demo.model.Student;
import com.example.demo.model.Teacher;
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
        lessonRepository.deleteById(id);
    }

    public void save(Lesson lesson, Long teacherId, Long studentId) {
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Teacher not found: " + teacherId));
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found: " + studentId));
        lesson.setTeacher(teacher);
        lesson.setStudent(student);
        LocalDateTime beforeLesson = lesson.getDatetime().minusMinutes(30);
        LocalDateTime afterLesson = lesson.getDatetime().plusMinutes(30);
        if (lesson.getDatetime().isAfter(LocalDateTime.now().plusMinutes(30))
                && !lessonRepository.existsByTeacherAndDatetimeBetween(teacher, beforeLesson, afterLesson)
                && !lessonRepository.existsByStudentAndDatetimeBetween(student, beforeLesson, afterLesson)) {
            lessonRepository.save(lesson);
        } else {
            throw new RuntimeException("Termin zajety lub nieprawidlowy");
        }
    }
}
