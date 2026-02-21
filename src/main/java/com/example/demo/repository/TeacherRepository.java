package com.example.demo.repository;

import com.example.demo.model.Language;
import com.example.demo.model.Teacher;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;


public interface TeacherRepository extends JpaRepository<Teacher, Long> {
//    private final List<Teacher> teachers = new ArrayList<>();
//    private int nextID = 1;

//    @PostConstruct
//    default void init() {
//        Teacher teacher1 = new Teacher(null, "Adrian", "Brygider");
//        save(teacher1);
//
//        Teacher teacher2 = new Teacher(null, "Terry", "Davis");
//        save(teacher2);
//        Teacher teacher3 = new Teacher(null, "Albert", "Einstein");
//        save(teacher3);
//
//    }
//
//    public List<Teacher> findAll();
//
//    public void deleteByID(int id);
}
