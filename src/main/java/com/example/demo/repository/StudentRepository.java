package com.example.demo.repository;

import com.example.demo.model.Student;
import com.example.demo.model.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {

    List<Student> findAllByTeacher(Teacher teacher);

    List<Student> findAllByDeletedFalse();

    Optional<Student> findByIdAndDeletedFalse(Long studentId);
}
