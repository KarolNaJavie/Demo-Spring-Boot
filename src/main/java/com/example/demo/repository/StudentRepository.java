package com.example.demo.repository;

import com.example.demo.model.Student;
import com.example.demo.model.Teacher;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

@Repository
public class StudentRepository {

    private final List<Student> students = new ArrayList<>();
    private int nextID = 1;

    @PostConstruct
    void init() {
        Student student1 = Student.builder().id(nextID).build();
        students.add(student1);
        nextID++;
        Student student2 = Student.builder().id(nextID).build();
        nextID++;
        students.add(student2);
        Student student3 = Student.builder().id(nextID).build();
        nextID++;
        students.add(student3);
    }

    public List<Student> findAll(){
        return students;
    }
    public void deleteByID(int id){
        students.removeIf(n->n.getId() == id);
    }

}
