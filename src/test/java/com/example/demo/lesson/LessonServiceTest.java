package com.example.demo.lesson;

import com.example.demo.model.*;
import com.example.demo.model.common.Language;
import com.example.demo.model.common.exception.LanguageMismatch;
import com.example.demo.model.common.exception.LessonCannotBeInThePastException;
import com.example.demo.model.common.exception.TermUnavailableException;
import com.example.demo.model.dto.LessonDTO;
import com.example.demo.repository.LessonRepository;
import com.example.demo.repository.StudentRepository;
import com.example.demo.repository.TeacherRepository;
import com.example.demo.service.LessonService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

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
       LessonDTO lesson1 = LessonDTO.builder().build();
        LessonDTO lesson2 = LessonDTO.builder().build();
        LessonDTO lesson3 = LessonDTO.builder().build();
        List<LessonDTO> lessons = List.of(lesson1, lesson2, lesson3);
        //zaprogramowanie mocka, kiedy ktos wywola findAll udawaj ze zwrociles liste
        when(lessonService.findAll()).thenReturn(lessons);

        List<LessonDTO> saved = lessonService.findAll();
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

        Lesson lesson = Lesson.builder().datetime(LocalDateTime.now().plusHours(1)).teacher(new Teacher()).id(lessonId).build();

        when(lessonRepository.findById(lessonId)).thenReturn(Optional.of(lesson));
        //nowy termin wolny (nie ma kolizji)
        when(lessonRepository.existsByTeacherAndDatetimeGreaterThanAndDatetimeLessThan(lesson.getTeacher(), oneHouerInThePast, oneHouerInTheFuture)).thenReturn(false);

        lessonService.changeDate(lessonId, newDateTime);
    }

    @Test
    void testChangeDate_TermUnavailable_ResultsInTermUnavailableException() {
        long lessonId = 1L;

        LocalDateTime newDateTime = LocalDateTime.now().plusHours(2);
        LocalDateTime oneHouerInTheFuture = newDateTime.plusHours(1);
        LocalDateTime oneHouerInThePast = newDateTime.minusHours(1);

        Lesson lesson = Lesson.builder().datetime(LocalDateTime.now().plusHours(1))
                .teacher(new Teacher()).id(lessonId).build();

        when(lessonRepository.findById(lessonId)).thenReturn(Optional.of(lesson));
        when(lessonRepository.existsByTeacherAndDatetimeGreaterThanAndDatetimeLessThan
                (lesson.getTeacher(), oneHouerInThePast, oneHouerInTheFuture)).thenReturn(true);
        assertThatExceptionOfType(TermUnavailableException.class)
                .isThrownBy(() -> lessonService.changeDate(1L, newDateTime))
                .withMessage("This date is not available");
        verify(lessonRepository, never()).save(any());
    }

    @Test
    void testFindById_LessonNotFound_ResultInEntityNotFoundException() {
        long lessonId = 1;
        String message = MessageFormat.format("Lesson with id={0} not found", lessonId);

        when(lessonRepository.findById(lessonId)).thenReturn(Optional.empty());

        assertThatExceptionOfType(EntityNotFoundException.class).isThrownBy(() -> lessonService.findById(lessonId)).withMessage(message);

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
        String exceptionMsg = MessageFormat.format("Lesson with id={0} not found", 1L);
        when(lessonRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatExceptionOfType(EntityNotFoundException.class).isThrownBy(() -> lessonService.deleteById(1L)).withMessage(exceptionMsg);
        verify(lessonRepository).findById(1L);
        verifyNoMoreInteractions(lessonRepository);
    }

    @Test
    void testSave_HappyPath_ResultsInLessonBeingSavedWithTeacherAndStudent() {
        Teacher teacher = Teacher.builder().id(1L).build();
        Student student = Student.builder().id(1L).build();
//        Lesson lesson = Lesson.builder().teacher(teacher).student(student).datetime(LocalDateTime.now().plusHours(1)).build();
        CreateLessonCommand createLessonCommand = CreateLessonCommand.builder().teacherId(1L).studentId(1L).build();
        when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher));
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));

        lessonService.save(createLessonCommand);
        verify(lessonRepository).save(lessonArgumentCaptor.capture());
        Lesson result = lessonArgumentCaptor.getValue();
        assertEquals(teacher.getId(), result.getTeacher().getId());
        assertEquals(student.getId(), result.getStudent().getId());
    }

    @Test
    void testSave_LanguageMismatch_ResultsInException() {
        Teacher teacher = Teacher.builder().languages(Set.of(Language.C)).id(1L).build();
        Student student = Student.builder().language(Language.JAVA).id(1L).build();
        CreateLessonCommand createLessonCommand = CreateLessonCommand.builder().teacherId(teacher.getId()).studentId(student.getId()).datetime(LocalDateTime.now().plusHours(1)).build();


        when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher));
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));

        assertThatExceptionOfType(LanguageMismatch.class)
                .isThrownBy(() -> lessonService.save(createLessonCommand))
                .withMessage("Languages of teacher and student dont match!");
        verify(lessonRepository, never()).save(any());
    }

    @Test
    void testSave_StudentNotFound_EntityNotFoundException() {
//        Lesson lesson = Lesson.builder().build();
        CreateLessonCommand createLessonCommand = CreateLessonCommand.builder().teacherId(1L).studentId(1L).build();
        String exceptionMsg = MessageFormat.format("Student with id={0} not found", 1L);
        when(studentRepository.findById(1L)).thenReturn(Optional.empty());
        assertThatExceptionOfType(EntityNotFoundException.class).isThrownBy(() -> lessonService.save(createLessonCommand)).withMessage(exceptionMsg);
        verify(studentRepository).findById(1L);
        verifyNoMoreInteractions(studentRepository);
        verifyNoInteractions(lessonRepository);
    }

    @Test
    void testSave_TeacherNotFound_EntityNotFoundException() {
//        Lesson lesson = Lesson.builder().build();
        CreateLessonCommand createLessonCommand = CreateLessonCommand.builder().teacherId(1L).studentId(1L).build();
        Student student = Student.builder().build();
        String exceptionMsg = MessageFormat.format("Teacher with id={0} not found", 1L);
        when(teacherRepository.findById(1L)).thenReturn(Optional.empty());
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        assertThatExceptionOfType(EntityNotFoundException.class).isThrownBy(() -> lessonService.save(createLessonCommand)).withMessage(exceptionMsg);
        verify(teacherRepository).findById(1L);
        verifyNoMoreInteractions(teacherRepository);
        verifyNoInteractions(lessonRepository);
    }

    @Test
    void testSave_DateInThePast_ResultsInLessonCannotBeInThePastException() {
        Student student = Student.builder().id(1L).build();
        Teacher teacher = Teacher.builder().id(1L).build();
        CreateLessonCommand createLessonCommand = CreateLessonCommand.builder().teacherId(1L).studentId(1L).datetime(LocalDateTime.now().minusHours(1)).build();
        String exceptionMsg = "Lesson cannot be in the past";
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher));
        assertThatExceptionOfType(LessonCannotBeInThePastException.class).isThrownBy(() -> lessonService.save(createLessonCommand)).withMessage(exceptionMsg);
        verifyNoInteractions(lessonRepository);
    }

    @Test
    void testSave_TermUnavailableException() {
        Student student = Student.builder().id(1L).build();
        Teacher teacher = Teacher.builder().id(1L).build();
//        Lesson lesson = Lesson.builder().datetime(LocalDateTime.now().plusHours(1)).build();
        CreateLessonCommand createLessonCommand = CreateLessonCommand.builder().teacherId(1L).studentId(1L).datetime(LocalDateTime.now().plusHours(1)).build();
        String exceptionMsg = "This date is not available";
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher));
        when(lessonRepository.existsByTeacherAndDatetimeGreaterThanAndDatetimeLessThan(any(), any(), any())).thenReturn(true);
        assertThatExceptionOfType(TermUnavailableException.class).isThrownBy(() -> lessonService.save(createLessonCommand)).withMessage(exceptionMsg);
        //             czy nigdy nie bylo uzyte save, repository jest uzywane do existby...
        verify(lessonRepository, never()).save(any());
    }
//    change date
}
