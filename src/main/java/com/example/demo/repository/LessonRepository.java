package com.example.demo.repository;

import com.example.demo.model.Lesson;
import com.example.demo.model.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {
    boolean existsByTeacherAndDatetimeGreaterThanAndDatetimeLessThan(Teacher teacher, LocalDateTime from, LocalDateTime to);
}
