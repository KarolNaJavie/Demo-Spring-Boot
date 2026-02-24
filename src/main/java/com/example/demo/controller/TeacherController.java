package com.example.demo.controller;

import com.example.demo.model.Language;
import com.example.demo.model.Teacher;
import com.example.demo.service.TeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Controller
@RequiredArgsConstructor
@RequestMapping("/teachers")
public class TeacherController {

    private final TeacherService teacherService;


//    @PostMapping("/create")
//    public String create(Teacher teacher){}

    @GetMapping("/list")
    public String getAll(Model model){
        model.addAttribute("teachers", teacherService.findAll());
        return "teachers/list";
    }
    @GetMapping("/{id}")
    public String delete(@PathVariable long id) {
        teacherService.deleteById(id);
        return "redirect:/teachers/list";
    }

    @GetMapping("/create")
    public String createFrom(Model model){
        model.addAttribute("languages", Language.values());
        return "teachers/register";
    }

    @PostMapping("/create")
    public String save (Teacher teacher){
        teacherService.save(teacher);
        return "redirect:/teachers/list";
    }

    @GetMapping("{id}/edit")
    public String editFrom(@PathVariable Long id, Model model){
        Teacher teacher = teacherService.findById(id);
        model.addAttribute("teacher", teacher);
        model.addAttribute("languages", Language.values());
        return "teachers/edit";
    }

    @PostMapping("{id}/edit")
    public String update(@PathVariable Long id, Teacher teacher){
        teacher.setId(id);
        teacherService.save(teacher);
        return "redirect:/teachers/list";
    }

    // Ściągawka z atrybutów Thymeleaf:
    //
    // ┌──────────┬────────────────────────┬───────────────────────────────────────────────────────────┐
    // │ Atrybut  │ Co robi                │ Przykład                                                  │
    // ├──────────┼────────────────────────┼───────────────────────────────────────────────────────────┤
    // │ th:text  │ Wstawia tekst          │ <p th:text="${student.name}">Jan</p>                      │
    // │ th:each  │ Pętla (jak for)        │ <tr th:each="s : ${students}">                            │
    // │ th:href  │ Dynamiczny link        │ <a th:href="@{/students/{id}(id=${student.id})}">Link</a> │
    // │ th:value │ Wartość inputa/selecta │ <input th:value="${student.name}">                        │
    // │ th:selected│ Zaznacza opcję       │ <option th:selected="${lang == student.language}">        │
    // │ th:if    │ Warunek (jak if)       │ <div th:if="${student.teacher != null}">                  │
    // └──────────┴────────────────────────┴───────────────────────────────────────────────────────────┘

}
