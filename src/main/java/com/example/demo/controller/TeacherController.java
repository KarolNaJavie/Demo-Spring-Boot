package com.example.demo.controller;

import com.example.demo.model.Teacher;
import com.example.demo.service.TeacherService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;


@Controller
@RequiredArgsConstructor
@RequestMapping("/Teachers")
public class TeacherController {

    private final TeacherService teacherService;

    @GetMapping("/all")
    public List<Teacher> getAll() {
        return teacherService.findAll();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) {
        teacherService.deleteByID(id);
    }


}
