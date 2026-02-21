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
    public String save (Student student){
        studentService.save(student);
        return "redirect:/students/list";
    }

    @GetMapping("{id}/edit")
    public String editFrom(@PathVariable Long id, Model model){
        Student student = studentService.findById(id);
        model.addAttribute("student", student);
        model.addAttribute("languages", Language.values());
        return "students/edit";
    }

    @PostMapping("{id}/edit")
    public String update (@PathVariable Long id, Student student){
        student.setId(id);
        studentService.save(student);
        return "redirect:/students/list";
    }

    //Teraz to samo co mamy w studencie chcemy mie dla teacher czyli DODAWANI USUWANIE I EDYCJE , narzie
    //pomijamy jezyki mozesz je zakomentowac w pizdu
    // formularze maja wygladac analogicznie
//    ⭐ ZADANIE BONUS (dla chętnych):
//    Dodaj stronę potwierdzenia po edycji:
//
//    Zamiast redirect na listę, pokaż stronę: "✅ Dane studenta zostały zaktualizowane!" z linkiem "Wróć do listy".
//
//
}
