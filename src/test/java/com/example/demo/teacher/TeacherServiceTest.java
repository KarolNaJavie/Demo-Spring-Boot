package com.example.demo.teacher;

import com.example.demo.model.Teacher;
import com.example.demo.repository.StudentRepository;
import com.example.demo.repository.TeacherRepository;
import com.example.demo.service.TeacherService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TeacherServiceTest {

    @InjectMocks
    private TeacherService teacherService;

    @Mock
    private TeacherRepository teacherRepository;
    @Mock
    private StudentRepository studentRepository;

    @Captor
    private ArgumentCaptor<Teacher> teacherArgumentCaptor;

    @Test
    void testFindAll_HappyPath_ResultsInAllTeachersBeingFound() {
        Teacher teacher1 = Teacher.builder().build();
        Teacher teacher2 = Teacher.builder().build();
        Teacher teacher3 = Teacher.builder().build();
        List<Teacher> teachers = List.of(teacher1, teacher2, teacher3);

        when(teacherService.findAll()).thenReturn(teachers);

        List<Teacher> saved = teacherService.findAll();

        verify(teacherRepository).findAll();
        assertEquals(saved, teachers);
    }
}
