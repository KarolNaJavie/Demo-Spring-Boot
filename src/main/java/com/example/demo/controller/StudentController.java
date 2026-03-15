package com.example.demo.controller;

import com.example.demo.model.Language;
import com.example.demo.model.Student;
import com.example.demo.model.Teacher;
import com.example.demo.model.dto.StudentDto;
import com.example.demo.service.StudentService;
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
@RequestMapping("/students")
public class StudentController {

    private final StudentService studentService;
    private final TeacherService teacherService;

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

    @GetMapping("/{id}/edit")
    public String getEditView(@PathVariable Long id, Model model) {
        Student student = studentService.findById(id);
        model.addAttribute("student", student);
        // Ladujemy tylko nauczycieli uczacych jezyka danego studenta
        // (filtrowani w TeacherRepository.findAllByLanguagesContaining)
        model.addAttribute("teachers", teacherService.findAllByLanguage(student.getLanguage()));
        return "student/edit";
    }

    /*
     * Ten endpoint jest wywolywany przez AJAX z formularza edycji studenta.
     * JavaScript wysyla: POST /students/{studentId}/changeTeacher/{teacherId}
     *
     * @ResponseBody void - nie zwracamy widoku ani przekierowania.
     * Spring odpowiada kodem HTTP 200 (OK) z pustym body.
     * JavaScript w szablonie sam wykonuje redirect po otrzymaniu odpowiedzi 200.
     */
    @PostMapping("/{studentId}/changeTeacher/{teacherId}")
    @ResponseBody
    public void changeTeacher(@PathVariable long studentId, @PathVariable long teacherId) {
        studentService.updateTeacher(studentId, teacherId);
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
    public List<StudentDto> findAllByTeacher(@RequestParam Teacher teacher) {
        return studentService.findAllByTeacher(teacher).stream()
                .map(StudentDto::fromEntity)
                .toList();
    }
}
