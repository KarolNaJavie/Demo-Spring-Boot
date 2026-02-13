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
        teacherService.deleteById(id);
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

    //Analogicznie do teachera dodac dla studenta:
    //id z odpowiednimi adnotacjami i adnotacja oznaczjaca encje, id bedzie generwoane (to samo co techaer)
    //stowrzyc tabele dla studenta tak samo jak teacher
    //wyprostowac controller - service - repo  zeby bylo jak dla teachera
    //ustawic repo dla studenta tak jak dla teachera korzystajac rowniez z jpa
    // usunac pozostalosci zeby projekt sie odpala i wszystkis mieci czyt. kom
    //narazie pominac lesson i relacje miedzy klasami dla uproszczenia
    // jpa ma taka metoda jak save, mozesz ja wykorzsytac aby zapisac do bazy nowego teachera, mozesz sprobowac zaimplementowac endpoint POST do tworzenia
    //nowego teachera/studenta cale flow jak dla innnych tylko tutaj na koncu wykorzsytaj metode teacherRepository.save(tu przekazujesz Techera)
    // gdzies na tym etapie nie bedziesz wiedzial czemu ci nie dziala albo co dalej, to wlasnie wtedy do mnie napisz :D

}
