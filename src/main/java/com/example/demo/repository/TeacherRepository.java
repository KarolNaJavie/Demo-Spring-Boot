package com.example.demo.repository;

import com.example.demo.model.Language;
import com.example.demo.model.Teacher;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@Slf4j
public class TeacherRepository {
    private final List<Teacher> teachers = new ArrayList<>();
    private int nextID = 1;

    @PostConstruct
    void init() {
        log.info("Inicjalizacja obiektow teacher");
        Teacher teacher1 = new Teacher(nextID, "Adrian", "Brygider", new ArrayList<>());
        teacher1.addLanguage(Language.JAVA);
        nextID++;
        teachers.add(teacher1);
        Teacher teacher2 = new Teacher(nextID, "Terry", "Davis", new ArrayList<>());
        teacher2.addLanguage(Language.HOLYC);
        nextID++;
        teachers.add(teacher2);
        Teacher teacher3 = new Teacher(nextID, "Albert", "Einstein", new ArrayList<>());
        nextID++;
        teachers.add(teacher3);
        teacher3.addLanguage(Language.C);
    }

    public List<Teacher> findAll() {
        return teachers;
    }
    public void deleteByID(int id){
        teachers.removeIf(n->n.getId() == id);
    }
}
