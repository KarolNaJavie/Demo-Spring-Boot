package com.example.demo.controller;

import com.example.demo.model.Lesson;
import com.example.demo.service.LessonService;
import com.example.demo.service.TeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
@RequestMapping("/lessons")
public class LessonController {

    private final LessonService lessonService;
    private final TeacherService teacherService;

    @GetMapping
    public String getAll(Model model) {
        model.addAttribute("lessons", lessonService.findAll());
        return "lesson/list";
    }

    @GetMapping("/create")
    public String createNew(Model model) {
        model.addAttribute("teachers", teacherService.findAll());
        return "lesson/create";
    }

    @PostMapping("/create")
    public String save(Lesson lesson, @RequestParam long studentId, @RequestParam long teacherId) {
        lessonService.save(lesson, studentId, teacherId);
        return "redirect:/lessons";
    }

    @GetMapping("/{id}")
    public String delete(@PathVariable Long id) {
        lessonService.deleteById(id);
        return "redirect:/lessons";
    }

    @GetMapping("/{id}/update")
    public String viewUpdateLesson(@PathVariable Long id, Model model) {
        Lesson existingLesson = lessonService.findById(id);
        model.addAttribute("lesson", existingLesson);
        return "lesson/edit";
    }

    @PostMapping("/{id}/update")
    public String updateLesson(@RequestParam Long lessonId,
                               @RequestParam @DateTimeFormat LocalDateTime newDate) {
        lessonService.changeDate(lessonId, newDate);
        return "redirect:/lessons";
    }
}
