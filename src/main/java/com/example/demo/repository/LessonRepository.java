package com.example.demo.repository;

import com.example.demo.model.Lesson;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class LessonRepository {
    private final List<Lesson> lessons = new ArrayList<>();
    private int nextID = 1;

    @PostConstruct
    void init(){
        Lesson lesson1 = Lesson.builder().id(nextID).build();
        nextID++;
        lessons.add(lesson1);
        Lesson lesson2 = Lesson.builder().id(nextID).build();
        nextID++;
        lessons.add(lesson2);
        Lesson lesson3 = Lesson.builder().id(nextID).build();
        nextID++;
        lessons.add(lesson3);
    }
    public List<Lesson> findAll(){
        return lessons;
    }

    public void deleteByID(int id){
        lessons.removeIf(n->n.getId() == id);
    }
}
