package com.example.demo.repository;

import com.example.demo.model.common.Language;
import com.example.demo.model.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    List<Teacher> findAllByLanguagesContaining(Language language);

    List<Teacher> findAllByDeletedFalse();

    Optional<Teacher> findByIdAndDeletedFalse(Long teacherId);
}
