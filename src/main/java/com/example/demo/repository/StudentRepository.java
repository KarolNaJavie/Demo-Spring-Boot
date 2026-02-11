package com.example.demo.repository;

import com.example.demo.model.Language;
import com.example.demo.model.Student;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class StudentRepository {

    private final List<Student> students = new ArrayList<>();
    private int nextID = 1;

    @PostConstruct
    void init() {
        Student student1 = new Student(nextID, "Karol", "Baranczyk", Language.JAVA);
        students.add(student1);
        nextID++;
        Student student2 = new Student(nextID, "Harry", "Potter", Language.HOLYC);
        nextID++;
        students.add(student2);
        Student student3 = new Student(nextID, "Big", "Lebowsky", Language.C);;
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
