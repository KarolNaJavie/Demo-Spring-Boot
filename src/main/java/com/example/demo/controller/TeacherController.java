package com.example.demo.controller;

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
    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) {
        teacherService.deleteByID(id);
    }

//              Atrybut  │ Co robi                │ Przykład                                                  │
//            ├──────────┼────────────────────────┼───────────────────────────────────────────────────────────┤
//            │ th:text  │ Wstawia tekst          │ <p th:text="${student.firstName}">Jan</p>                 │
//            ├──────────┼────────────────────────┼───────────────────────────────────────────────────────────┤
//            │ th:each  │ Pętla (jak for)        │ <tr th:each="s : ${students}">                            │
//            ├──────────┼────────────────────────┼───────────────────────────────────────────────────────────┤
//            │ th:href  │ Dynamiczny link        │ <a th:href="@{/students/{id}(id=${student.id})}">Link</a> │
//            ├──────────┼────────────────────────┼───────────────────────────────────────────────────────────┤
//            │ th:value │ Wartość inputa/selecta │ <option th:value="${language}">                           │
//            ├──────────┼────────────────────────┼───────────────────────────────────────────────────────────┤
//            │ th:if    │ Warunek (jak if)       │ <div th:if="${student.teacher != null}">                  │
//            └──────────┴────────────────────────┴──────────────────────────────────────


    //DO ZROBIENIA NA CZWARTEK:
    // ZROB WIECEJ OBIEKTOW ZEBY BYLO CO WYSWIETLIC (Z PELNA ILOSC POL)
    //DODAJ logowanie SLF4J tak jak w teacher repo do innych repo
    //STWORZ WIZUALIZACJE TABEL DLA LESSON I STUNDENT (OSOBNE HTML)
    //SQL DEVELOPER ALBO SQL WORKBENCH I DOCKER


}
