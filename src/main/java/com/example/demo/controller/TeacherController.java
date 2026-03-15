package com.example.demo.controller;

import com.example.demo.model.Language;
import com.example.demo.model.Teacher;
import com.example.demo.model.dto.TeacherDto;
import com.example.demo.service.TeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/teachers")
public class TeacherController {

    private final TeacherService teacherService;

    @GetMapping
    public String getAll(Model model) {
        model.addAttribute("teachers", teacherService.findAll());
        return "teacher/list";
    }

    @GetMapping("/create")
    public String createNew(Model model) {
        model.addAttribute("languages", Language.values());
        return "teacher/register";
    }

    @PostMapping("/create")
    public String save(Teacher teacher) {
        teacherService.save(teacher);
        return "redirect:/teachers";
    }

    @GetMapping("/{id}")
    public String delete(@PathVariable Long id) {
        teacherService.deleteById(id);
        return "redirect:/teachers";
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
    public List<TeacherDto> findAllByLanguage(@RequestParam Language language) {
        return teacherService.findAllByLanguage(language).stream()
                .map(TeacherDto::fromEntity)
                .toList();
    }
}
