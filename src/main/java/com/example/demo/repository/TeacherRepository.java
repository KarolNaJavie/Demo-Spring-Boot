package com.example.demo.repository;

import com.example.demo.model.Language;
import com.example.demo.model.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    List<Teacher> findAllByLanguagesContaining(Language language);
}
