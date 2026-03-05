package com.example.demo.repository;

import com.example.demo.model.Lesson;
import com.example.demo.model.Student;
import com.example.demo.model.Teacher;
import jakarta.annotation.PostConstruct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {


    boolean existsByTeacherAndDatetimeBetween(Teacher teacher, LocalDateTime localDateTime, LocalDateTime localDateTime1);

    boolean existsByStudentAndDatetimeBetween(Student student, LocalDateTime localDateTime, LocalDateTime localDateTime1);

    boolean existsByTeacherAndDatetimeGreaterThanAndDatetimeLessThan(Teacher teacher, LocalDateTime from, LocalDateTime to);
}
