package com.example.demo.lesson;

import com.example.demo.model.Lesson;
import com.example.demo.model.Student;
import com.example.demo.model.Teacher;
import com.example.demo.repository.LessonRepository;
import com.example.demo.repository.StudentRepository;
import com.example.demo.repository.TeacherRepository;
import com.example.demo.service.LessonService;
import jakarta.persistence.EntityNotFoundException;
import net.bytebuddy.asm.Advice;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static tools.jackson.databind.type.LogicalType.DateTime;

@ExtendWith(MockitoExtension.class)
class LessonServiceTest {

    @InjectMocks
    private LessonService lessonService;

    @Mock
    private LessonRepository lessonRepository;
    @Mock
    private TeacherRepository teacherRepository;
    @Mock
    private StudentRepository studentRepository;

    @Captor
    private ArgumentCaptor<Lesson> lessonArgumentCaptor;

    @Test
    void testFindAll_HappyPath_ResultsInAllLessonBeingFound() {
        //given - przygotowanie testowych danych
        Lesson lesson1 = Lesson.builder().build();
        Lesson lesson2 = Lesson.builder().build();
        Lesson lesson3 = Lesson.builder().build();
        List<Lesson> lessons = List.of(lesson1, lesson2, lesson3);
        //zaprogramowanie mocka, kiedy ktos wywola findAll udawaj ze zwrociles liste
        when(lessonService.findAll()).thenReturn(lessons);

        List<Lesson> saved = lessonService.findAll();
        //then, verify sprawdza czy mock zoszal wywolany i ile razy
        //jesli nie service nie wywola findAll to test padnie
        verify(lessonRepository).findAll();
        assertEquals(saved, lessons);

        //napisac analogiczny test dla teachera findall, teacher findAllByLanguage, save(lesson i teacher)-- potrzeba ArgumentCaptor
        // --        verify(teacherRepository).save(teacherArgumentCaptor.capture());
        // teacherArgumentCaptor.getValue();


    }

    @Test
    void testChangeDate_HappyPath_ResultrsInLessonWithChangedDate() {
        //Given
        long lessonId = 1L;

        LocalDateTime newDateTime = LocalDateTime.now().plusHours(2); //nowy termin w przyszlosci
        LocalDateTime oneHouerInTheFuture = newDateTime.plusHours(1);
        LocalDateTime oneHouerInThePast = newDateTime.minusHours(1);

        //Stary termin lekcji musi byc tez w przyszlosci  ( bo lekcja nie moze byc juz rozpoczeta)

        Lesson lesson = Lesson.builder()
                .datetime(LocalDateTime.now().plusHours(1))
                .teacher(new Teacher())
                .id(lessonId)
                .build();

        when(lessonRepository.findById(lessonId)).thenReturn(Optional.of(lesson));
        //nowy termin wolny (nie ma kolizji)
        when(lessonRepository.existsByTeacherAndDatetimeGreaterThanAndDatetimeLessThan(lesson.getTeacher()
                , oneHouerInThePast, oneHouerInTheFuture)).thenReturn(false);

        lessonService.changeDate(lessonId, newDateTime);


    }

    @Test
    void testFindById_LessonNotFound_ResultInEntityNotFoundException() {
        long lessonId = 1;
        String message = MessageFormat.format("Lesson with id={0} not found", lessonId);

        when(lessonRepository.findById(lessonId)).thenReturn(Optional.empty());

        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> lessonService.findById(lessonId))
                .withMessage(message);

        verify(lessonRepository).findById(lessonId);
        verifyNoMoreInteractions(lessonRepository);
    }

    @Test
    void testDeleteById_HappyPath_ResultIn() {
        Lesson lesson = Lesson.builder().datetime(LocalDateTime.now().plusHours(1)).build();
        Long lessonId = 1L;
        when(lessonRepository.findById(lessonId)).thenReturn(Optional.of(lesson));
        lessonService.deleteById(1L);
        verify(lessonRepository).deleteById(lessonId);

    }

    //dokonczyc testy
    // do sprawdzenia
    @Test
    void testDeleteById_LessonNotFound_EntityNotFoundException() {
        String msg = MessageFormat.format("Lesson with id={0} not found", 1L);
        when(lessonRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> lessonService.deleteById(1L))
                .withMessage(msg);
        verify(lessonRepository).findById(1L);
        verifyNoMoreInteractions(lessonRepository);
    }

    @Test
    void testSave_HappyPath_ResultsInLessonBeingSavedWithTeacherAndStudent() {
        Teacher teacher = Teacher.builder().id(1L).build();
        Student student = Student.builder().id(1L).build();
        Lesson lesson = Lesson.builder().teacher(teacher).student(student).datetime(LocalDateTime.now().plusHours(1)).build();


        when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher));
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));

        lessonService.save(lesson, 1L, 1L);
        verify(lessonRepository).save(lesson);
        assertEquals(teacher, lesson.getTeacher());
        assertEquals(student, lesson.getStudent());
    }

    @Test
    void testSave_StudentNotFound_EntityNotFoundException() {
        Lesson lesson = Lesson.builder().build();
        String msg = MessageFormat.format("Student with id={0} not found", 1L);
        when(studentRepository.findById(1L)).thenReturn(Optional.empty());
        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> lessonService.save(lesson, 1L, 1L))
                .withMessage(msg);
        verify(studentRepository).findById(1L);
        verifyNoMoreInteractions(studentRepository);
        verifyNoInteractions(lessonRepository);
    }

    @Test
    void testSave_TeacherNotFound_EntityNotFoundException() {
        Lesson lesson = Lesson.builder().build();
        Student student = Student.builder().build();
        String msg = MessageFormat.format("Teacher with id={0} not found", 1L);
        when(teacherRepository.findById(1L)).thenReturn(Optional.empty());
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> lessonService.save(lesson, 1L, 1L))
                .withMessage(msg);
        verify(teacherRepository).findById(1L);
        verifyNoMoreInteractions(teacherRepository);
        verifyNoInteractions(lessonRepository);
    }

}
