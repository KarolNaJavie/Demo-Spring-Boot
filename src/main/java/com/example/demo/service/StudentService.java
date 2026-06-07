package com.example.demo.service;

import com.example.demo.model.CreateStudentCommand;
import com.example.demo.model.Student;
import com.example.demo.model.Teacher;
import com.example.demo.model.common.exception.LanguageMismatch;
import com.example.demo.model.dto.StudentDTO;
import com.example.demo.repository.StudentRepository;
import com.example.demo.repository.TeacherRepository;
import jakarta.persistence.EntityNotFoundException;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;

    @Transactional(readOnly = true)
    public List<StudentDTO> findAllActive() {
        return studentRepository.findAllByDeletedFalse()
                .stream()
                .map(StudentDTO::fromEntity)
                .toList();
    }

    @Transactional
    public StudentDTO save(CreateStudentCommand createStudentCommand) {
        Teacher teacher = teacherRepository.findById(createStudentCommand.getTeacherId()).orElseThrow(() -> new EntityNotFoundException("Teacher not found"));
        Student student = createStudentCommand.toEntity();
        validateTeacherLanguage(student, teacher);
        student.setTeacher(teacher);
        return StudentDTO.fromEntity(studentRepository.save(student));
    }

    @Transactional
    public void deleteById(Long id) {
        studentRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Student findById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(MessageFormat
                        .format("Student with id={0} not found", id)));
    }

    @Transactional(readOnly = true)
    public List<Student> findAllByTeacher(Teacher teacher) {
        return studentRepository.findAllByTeacher(teacher);
    }

    public void softDelete(long id) {
        Student student = studentRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(MessageFormat
                .format("Teacher with id={0} not found", id)));
        student.setDeleted(true);
        studentRepository.save(student);
    }


    public StudentDTO update(@Valid CreateStudentCommand command, Long id) {
        Student student = studentRepository.findByIdAndDeletedFalse(id).orElseThrow(() ->
                new EntityNotFoundException(MessageFormat.format("Student with id={0} not found", id)));
        Teacher teacher = teacherRepository.findByIdAndDeletedFalse(command.getTeacherId()).orElseThrow(() ->
                new EntityNotFoundException(MessageFormat.format("Teacher with id={0} not found", id)));
        validateTeacherLanguage(student, teacher);
        student.setTeacher(teacher);
        student.setFirstName(command.getFirstName());
        student.setLastName(command.getLastName());
        student.setLanguage(command.getLanguage());
        return StudentDTO.fromEntity(studentRepository.save(student));
    }

    public void validateTeacherLanguage(Student student, Teacher teacher) {
        if (!teacher.getLanguages().contains(student.getLanguage())) {
            throw new LanguageMismatch();
        }
    }
}

