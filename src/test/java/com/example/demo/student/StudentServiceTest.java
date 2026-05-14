package com.example.demo.student;


import com.example.demo.model.CreateStudentCommand;
import com.example.demo.model.Student;
import com.example.demo.model.Teacher;
import com.example.demo.model.dto.StudentDTO;
import com.example.demo.repository.StudentRepository;
import com.example.demo.repository.TeacherRepository;
import com.example.demo.service.StudentService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StudentServiceTest {

    @InjectMocks
    private StudentService studentService;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private TeacherRepository teacherRepository;

    @Captor
    private ArgumentCaptor<Student> studentArgumentCaptor;
    @Captor
    private ArgumentCaptor<Teacher> teacherCaptor;

    @Test
    void testFindAll_HappyPath_ResultInAllStudentsBeingFound() {
        StudentDTO student1 = StudentDTO.builder().build();
        StudentDTO student2 = StudentDTO.builder().build();
        StudentDTO student3 = StudentDTO.builder().build();
        List<StudentDTO> students = List.of(student1, student2, student3);

        when(studentService.findAllActive()).thenReturn(students);
        List<StudentDTO> saved = studentService.findAllActive();
        verify(studentRepository).findAll();

        assertEquals(saved, students);
    }

    @Test
    void testFindById_StudentNotFound_ResultInEntityNotFoundException() {
        long studentId = 1;
        String exceptionMsg = MessageFormat.format("Student with id={0} not found", studentId);

        when(studentRepository.findById(studentId)).thenReturn(Optional.empty());
        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> studentService.findById(studentId)).withMessage(exceptionMsg);
        verify(studentRepository).findById(studentId);
        verifyNoMoreInteractions(studentRepository);
    }

    @Test
    void testFindAllByTeacher_HappyPath_ResultsInAllStudentsByTeacherBeingFound() {
        Teacher teacher = Teacher.builder().build();
        Student student = Student.builder().teacher(teacher).build();
        List<Student> students = List.of(student);

        when(studentRepository.findAllByTeacher(teacher)).thenReturn(students);
        System.out.println(teacher);

        studentService.findAllByTeacher(teacher);

        verify(studentRepository).findAllByTeacher(teacherCaptor.capture());
        assertEquals(teacher, teacherCaptor.getValue());
    }

    @Test
    void testFindAllByTeacher_HappyPath_ResultsInStudentsBeingReturned() {
        Teacher teacher = Teacher.builder().id(2L).build();
        List<Student> students = List.of(
                Student.builder().id(1L).build(),
                Student.builder().id(2L).build()
        );

        when(studentRepository.findAllByTeacher(teacher)).thenReturn(students);

        List<Student> result = studentService.findAllByTeacher(teacher);

        assertEquals(2, result.size());
        verify(studentRepository).findAllByTeacher(teacher);
    }

    @Test
    void testSave_HappyPath_ResultsInStudentBeingSaved() {
        Teacher teacher = Teacher.builder().id(1L).build();
//        Student student = Student.builder().firstName("Johny").lastName("Bravo").id(1L).build();
        CreateStudentCommand createStudentCommand = CreateStudentCommand.builder().teacherId(teacher.getId()).build();
        when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher));
        StudentDTO studentDTO = studentService.save(createStudentCommand);

        verify(studentRepository).save(createStudentCommand.toEntity());
        assertEquals(createStudentCommand.getFirstName(), studentDTO.getFirstName());
    }

    @Test
    void testSave_TeacherNotFound_ResultInEntityNotFoundException() {
        CreateStudentCommand createStudentCommand = CreateStudentCommand.builder().teacherId(1L).build();
        String exceptionMsg = MessageFormat.format("Teacher with id={0} not found", 1L);
        when(teacherRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> studentService.save(createStudentCommand))
                .withMessage(exceptionMsg);
        verify(teacherRepository).findById(1L);
        verifyNoMoreInteractions(teacherRepository);
        verifyNoInteractions(studentRepository);
    }
}
