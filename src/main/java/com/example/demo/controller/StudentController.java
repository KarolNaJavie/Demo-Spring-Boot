package com.example.demo.controller;

import com.example.demo.model.CreateStudentCommand;
import com.example.demo.model.common.Language;
import com.example.demo.model.Student;
import com.example.demo.model.Teacher;
import com.example.demo.model.dto.StudentDTO;
import com.example.demo.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/students")
public class StudentController {

    private final StudentService studentService;

    @GetMapping
    public List<StudentDTO> getAll() {
        return studentService.findAllActive();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StudentDTO create(@RequestBody @Valid CreateStudentCommand createStudentCommand) {
        return studentService.save(createStudentCommand);
    }

    @PutMapping("/{id}")
    public StudentDTO update(@RequestBody @Valid CreateStudentCommand command, @PathVariable Long id) {
        return studentService.update(command, id);
    }

    @DeleteMapping("/{id}")
    public void softDelete(@PathVariable Long id) {
        studentService.softDelete(id);
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

    //    wrocic tu!
    @GetMapping(params = "teacher")
    @ResponseBody
    public List<StudentDTO> findAllByTeacher(@RequestParam Teacher teacher) {
        return studentService.findAllByTeacher(teacher).stream().map(StudentDTO::fromEntity).toList();
    }
}
