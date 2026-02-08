package com.example.demo.controller;

import com.example.demo.model.Lesson;
import com.example.demo.service.LessonService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/Lessons")
public class LessonController {
    private final LessonService lessonService;

    @GetMapping("/All")
    public List<Lesson> getAll(){
        return lessonService.findAll();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) {
        lessonService.deleteByID(id);
    }
}
