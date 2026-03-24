package com.example.demo.controller;

import com.example.demo.model.Language;
import com.example.demo.model.Student;
import com.example.demo.model.Teacher;
import com.example.demo.model.dto.StudentDTO;
import com.example.demo.service.StudentService;
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
@RequestMapping("/students")
public class StudentController {

    private final StudentService studentService;

    @GetMapping
    public String getAll(Model model) {
        model.addAttribute("students", studentService.findAll());
        return "student/list";
    }

    @GetMapping("/create")
    public String createNew(Model model) {
        model.addAttribute("languages", Language.values());
        return "student/register";
    }

    @PostMapping("/create")
    public String save(Student student, @RequestParam long teacherId) {
        studentService.save(student, teacherId);
        return "redirect:/students";
    }

    @GetMapping("/{id}")
    public String delete(@PathVariable Long id) {
        studentService.deleteById(id);
        return "redirect:/students";
    }

    /*
     * Ten endpoint jest wywolywany przez AJAX z formularza tworzenia lekcji.
     * Gdy uzytkownik wybierze nauczyciela, JavaScript wysyla:
     *   GET /students?teacher=1
     *
     * @RequestParam Teacher teacher - Spring Data automatycznie zamienia
     * przekazane id (np. "1") na obiekt Teacher z bazy danych (DomainClassConverter).
     *
     * @ResponseBody - zwracamy JSON, nie widok Thymeleaf.
     */
    @GetMapping(params = "teacher")
    @ResponseBody
    public List<StudentDTO> findAllByTeacher(@RequestParam Teacher teacher) {
        return studentService.findAllByTeacher(teacher).stream()
                .map(StudentDTO::fromEntity)
                .toList();
    }
}
