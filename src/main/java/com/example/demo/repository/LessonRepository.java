package com.example.demo.repository;

import com.example.demo.model.Lesson;
import jakarta.annotation.PostConstruct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {

}
