package com.example.demo.repository;

import com.example.demo.model.Teacher;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class TeacherRepository {
    private final List<Teacher> teachers = new ArrayList<>();
    private int nextID = 1;

    @PostConstruct
    void init() {
        Teacher teacher1 = new Teacher(nextID, "Adrian", "Brygider", new ArrayList<>());
        nextID++;
        teachers.add(teacher1);
        Teacher teacher2 = new Teacher(nextID, null, null, new ArrayList<>());
        nextID++;
        teachers.add(teacher2);
        Teacher teacher3 = Teacher.builder().id(nextID).build();
        nextID++;
        teachers.add(teacher3);
    }

    public List<Teacher> findAll() {
        return teachers;
    }
    public void deleteByID(int id){
        teachers.removeIf(n->n.getId() == id);
    }
}
