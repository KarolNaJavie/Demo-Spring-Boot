package com.example.demo.controller;

import com.example.demo.model.CreateTeacherCommand;
import com.example.demo.model.common.Language;
import com.example.demo.model.Teacher;
import com.example.demo.model.dto.TeacherDTO;
import com.example.demo.service.TeacherService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/teachers")
public class TeacherController {

    private final TeacherService teacherService;

    @GetMapping
    public List<TeacherDTO> getAll() {
        return teacherService.findAllActive();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)  // dodac validacje
    public TeacherDTO create(@RequestBody @Valid CreateTeacherCommand createTeacherCommand) {
        return teacherService.save(createTeacherCommand);
    }

//    @GetMapping("/{id}")
//    public String delete(@PathVariable Long id) {
//        teacherService.deleteById(id);
//        return "redirect:/teachers";
//    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void softDelete(@PathVariable Long id) {
        teacherService.softDelete(id);
    }


    /*
     * Ten endpoint jest wywolywany przez AJAX z formularza tworzenia studenta.
     * Gdy uzytkownik wybierze jezyk, JavaScript wysyla:
     *   GET /teachers?language=JAVA
     *
     * params = "language" oznacza: ten mapping aktywuje sie tylko gdy w URL
     * jest parametr "language". Bez tego Spring uzylby metody getAll() powyzej.
     *
     * @ResponseBody - zamiast zwracac nazwe widoku Thymeleaf, Spring serializuje
     * zwracana liste do JSON i wysyla bezposrednio w ciele odpowiedzi HTTP.
     * To wlasnie JavaScript odbiera po stronie przegladarki.
     */
    @GetMapping(params = "language")
    @ResponseBody
    public List<TeacherDTO> findAllByLanguage(@RequestParam Language language) {
        return teacherService.findAllByLanguage(language);
    }
}
