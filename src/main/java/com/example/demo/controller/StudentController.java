package com.example.demo.controller;

import com.example.demo.model.Language;
import com.example.demo.model.Student;
import com.example.demo.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/students")
public class StudentController {
    private final StudentService studentService;

    @GetMapping("/list")
    public String getAll(Model model) {
        model.addAttribute("students", studentService.findAll());
        return "students/list";
    }

    @GetMapping("/{id}")
    public String delete(@PathVariable long id) {
        studentService.deleteById(id);
        return "redirect:/students/list";
    }

    @GetMapping("/create")
    public String createFrom(Model model) {
        model.addAttribute("languages", Language.values());
        return "students/register";
    }

    @PostMapping("/create")
    public String save (Student student, Long teacherId){
        studentService.save(student, teacherId);
        return "redirect:/students/list";
    }

    @GetMapping("{id}/edit")
    public String editFrom(@PathVariable Long id, Model model){
        Student student = studentService.findById(id);
        model.addAttribute("student", student);
        model.addAttribute("languages", Language.values());
        //jak dla languages findall z teacherow
        return "students/edit";
    }

    @PostMapping("{id}/edit")
    public String update (@PathVariable Long id, Student student, Long teacherId){
        student.setId(id);
        studentService.save(student, teacherId);
        return "redirect:/students/list";
    }

    // ⭐ ZADANIE BONUS (dla chętnych):
    // Zamiast redirect na listę po edycji, pokaż stronę z potwierdzeniem:
    // "✅ Dane studenta zostały zaktualizowane!" z linkiem "Wróć do listy".

}
