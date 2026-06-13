package com.example.demo.controller;

import com.example.demo.model.CreateLessonCommand;
import com.example.demo.model.Lesson;
import com.example.demo.model.dto.LessonDTO;
import com.example.demo.service.LessonService;
import com.example.demo.service.TeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/lessons")
public class LessonController {

    private final LessonService lessonService;
    private final TeacherService teacherService;

    @GetMapping
    public List<LessonDTO> getAll() {
        return lessonService.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LessonDTO create(@RequestBody CreateLessonCommand createLessonCommand) {
        return lessonService.save(createLessonCommand);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        lessonService.deleteById(id);
    }

    @PutMapping("/{id}")
    public LessonDTO updateLesson(@PathVariable Long id, @RequestParam @DateTimeFormat LocalDateTime newDate) {
        return lessonService.changeDate(id, newDate);
    }
}
