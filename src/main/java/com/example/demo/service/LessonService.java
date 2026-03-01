package com.example.demo.service;

import com.example.demo.model.Lesson;
import com.example.demo.model.Student;
import com.example.demo.model.Teacher;
import com.example.demo.repository.LessonRepository;
import com.example.demo.repository.StudentRepository;
import com.example.demo.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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
        Teacher teacher = teacherRepository.findById(teacherId).orElseThrow(()-> new RuntimeException("Teacher not found: " + teacherId));
        Student student = studentRepository.findById(studentId).orElseThrow(()-> new RuntimeException("Student not found: " + studentId));
        lesson.setTeacher(teacher);
        lesson.setStudent(student);
        lessonRepository.save(lesson);
    }
}
