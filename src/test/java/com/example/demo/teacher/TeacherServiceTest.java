package com.example.demo.teacher;

import com.example.demo.model.Language;
import com.example.demo.model.Teacher;
import com.example.demo.repository.StudentRepository;
import com.example.demo.repository.TeacherRepository;
import com.example.demo.service.TeacherService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

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

    @Captor
    private ArgumentCaptor<Language> teacherLanguageCaptor;


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

    @Test
    void testFindAll_HappyPath_ResultsInAllTeachersByLanguageBeingFound() {
        Language c = Language.C;
        Teacher teacher1 = Teacher.builder().languages(Set.of(c)).build();

        //when - symulujemy bazy
        when(teacherRepository.findAllByLanguagesContaining(c)).thenReturn(List.of(teacher1));

        //realne wywolanie meotdy w serwisie
        teacherService.findAllByLanguage(c);

        //then weryfikujemy ze serwis wywolal metode z wlasciwym argumentem
        verify(teacherRepository).findAllByLanguagesContaining(teacherLanguageCaptor.capture());
        assertEquals(c, teacherLanguageCaptor.getValue());
    }

    @Test
    void testFindById_TeacherNotFound_ResultInEntityNotFoundException() {
        long teacherId = 1L;
        String exceptionMsg = MessageFormat.format("Teacher with id={0} not found", teacherId);

        when(teacherRepository.findById(teacherId)).thenReturn(Optional.empty());


        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> teacherService.findById(teacherId))
                .withMessage(exceptionMsg);
        //Sprawdzamy czy:
        //-wyjatek zostal rzucony
        //- czy jest odpowiedniego typu
        //- czy ma odpowiedni message

        verify(teacherRepository).findById(teacherId);
        verifyNoMoreInteractions(teacherRepository);// sorawdza czy serwis nie wchodzil juz w interakcje po rzuceniu wyjatku
    }


}