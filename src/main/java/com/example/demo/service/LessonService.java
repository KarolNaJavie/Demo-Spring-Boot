package com.example.demo.service;

import com.example.demo.model.Lesson;
import com.example.demo.repository.LessonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LessonService {
    private final LessonRepository lessonRepository;

    public List<Lesson> findAll(){
        return lessonRepository.findAll();
    }

    public void deleteByID(int id) {
        lessonRepository.deleteByID(id);
    }
}
