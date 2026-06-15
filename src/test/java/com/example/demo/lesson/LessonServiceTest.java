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
        Teacher teacher = Teacher.builder().id(1L).build();
        Student student = Student.builder().id(1L).build();
       Lesson lesson1 = Lesson.builder().student(student).teacher(teacher).build();
        Lesson lesson2 = Lesson.builder().student(student).teacher(teacher).build();
        Lesson lesson3 = Lesson.builder().student(student).teacher(teacher).build();
        List<Lesson> lessons = List.of(lesson1, lesson2, lesson3);
        //zaprogramowanie mocka, kiedy ktos wywola findAll udawaj ze zwrociles liste
        when(lessonRepository.findAll()).thenReturn(lessons);

        List<LessonDTO> saved = lessonService.findAll();
        //then, verify sprawdza czy mock zoszal wywolany i ile razy
        //jesli nie service nie wywola findAll to test padnie
        verify(lessonRepository).findAll();
        assertEquals(saved.get(0).getStudentId(), lessons.get(0).getStudent().getId());
    }

    @Test
    void testChangeDate_HappyPath_ResultrsInLessonWithChangedDate() {
        //Given
        long lessonId = 1L;

        LocalDateTime newDateTime = LocalDateTime.now().plusHours(2); //nowy termin w przyszlosci
        LocalDateTime oneHouerInTheFuture = newDateTime.plusHours(1);
        LocalDateTime oneHouerInThePast = newDateTime.minusHours(1);

        //Stary termin lekcji musi byc tez w przyszlosci  ( bo lekcja nie moze byc juz rozpoczeta)

        Lesson lesson = Lesson.builder().student(Student.builder().id(1L).build()).datetime(LocalDateTime.now().plusHours(1)).teacher(new Teacher()).id(1L).build();

        when(lessonRepository.findById(lessonId)).thenReturn(Optional.of(lesson));
        //nowy termin wolny (nie ma kolizji)
        when(lessonRepository.existsByTeacherAndDatetimeGreaterThanAndDatetimeLessThanAndIdNot(lesson.getTeacher(), oneHouerInThePast, oneHouerInTheFuture, lessonId)).thenReturn(false);

        LessonDTO lessonDTO = lessonService.changeDate(lessonId, newDateTime);

        assertEquals(lessonDTO.getDatetime(), newDateTime);
    }

    @Test
    void testChangeDate_TermUnavailable_ResultsInTermUnavailableException() {
        long lessonId = 1L;

        LocalDateTime newDateTime = LocalDateTime.now().plusHours(2);
        LocalDateTime oneHouerInTheFuture = newDateTime.plusHours(1);
        LocalDateTime oneHouerInThePast = newDateTime.minusHours(1);

        Lesson lesson = Lesson.builder().datetime(LocalDateTime.now().plusHours(1))
                .student(Student.builder().build()).teacher(new Teacher()).id(lessonId).build();

        when(lessonRepository.findById(lessonId)).thenReturn(Optional.of(lesson));
        when(lessonRepository.existsByTeacherAndDatetimeGreaterThanAndDatetimeLessThanAndIdNot
                (lesson.getTeacher(), oneHouerInThePast, oneHouerInTheFuture, lessonId)).thenReturn(true);
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

    @Test
    void testDeleteById_LessonNotFound_EntityNotFoundException() {
        String exceptionMsg = MessageFormat.format("Lesson with id={0} not found", 1L);
        when(lessonRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatExceptionOfType(EntityNotFoundException.class).isThrownBy(() -> lessonService.deleteById(1L)).withMessage(exceptionMsg);
        verify(lessonRepository).findById(1L);
        verifyNoMoreInteractions(lessonRepository);
    }

    @Test
    void testSave_LanguageMismatch_ResultsInException() {
        Teacher teacher = Teacher.builder().languages(Set.of(Language.C)).id(1L).build();
        Student student = Student.builder().language(Language.JAVA).id(1L).build();
        CreateLessonCommand createLessonCommand = CreateLessonCommand.builder().teacherId(teacher.getId()).studentId(student.getId()).datetime(LocalDateTime.now().plusHours(1)).build();


        when(teacherRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(teacher));
        when(studentRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(student));

        assertThatExceptionOfType(LanguageMismatch.class)
                .isThrownBy(() -> lessonService.save(createLessonCommand))
                .withMessage("Languages of teacher and student dont match!");
        verify(lessonRepository, never()).save(any());
    }

    @Test
    void testSave_StudentNotFound_EntityNotFoundException() {
        CreateLessonCommand createLessonCommand = CreateLessonCommand.builder().teacherId(1L).studentId(1L).build();
        String exceptionMsg = MessageFormat.format("Student with id={0} not found", 1L);
        when(studentRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.empty());
        assertThatExceptionOfType(EntityNotFoundException.class).isThrownBy(() -> lessonService.save(createLessonCommand)).withMessage(exceptionMsg);
        verify(studentRepository).findByIdAndDeletedFalse(1L);
        verifyNoMoreInteractions(studentRepository);
        verifyNoInteractions(lessonRepository);
    }

    @Test
    void testSave_TeacherNotFound_EntityNotFoundException() {
        CreateLessonCommand createLessonCommand = CreateLessonCommand.builder().teacherId(1L).studentId(1L).build();
        Student student = Student.builder().build();
        String exceptionMsg = MessageFormat.format("Teacher with id={0} not found", 1L);
        when(teacherRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.empty());
        when(studentRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(student));
        assertThatExceptionOfType(EntityNotFoundException.class).isThrownBy(() -> lessonService.save(createLessonCommand)).withMessage(exceptionMsg);
        verify(teacherRepository).findByIdAndDeletedFalse(1L);
        verifyNoMoreInteractions(teacherRepository);
        verifyNoInteractions(lessonRepository);
    }

    @Test
    void testSave_DateInThePast_ResultsInLessonCannotBeInThePastException() {
        Student student = Student.builder().id(1L).build();
        Teacher teacher = Teacher.builder().id(1L).build();
        CreateLessonCommand createLessonCommand = CreateLessonCommand.builder().teacherId(1L).studentId(1L).datetime(LocalDateTime.now().minusHours(1)).build();
        String exceptionMsg = "Lesson cannot be in the past";
        when(studentRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(student));
        when(teacherRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(teacher));
        assertThatExceptionOfType(LessonCannotBeInThePastException.class).isThrownBy(() -> lessonService.save(createLessonCommand)).withMessage(exceptionMsg);
        verifyNoInteractions(lessonRepository);
    }

    @Test
    void testSave_TermUnavailableException() {
        Student student = Student.builder().id(1L).build();
        Teacher teacher = Teacher.builder().id(1L).build();
        CreateLessonCommand createLessonCommand = CreateLessonCommand.builder().teacherId(1L).studentId(1L).datetime(LocalDateTime.now().plusHours(1)).build();
        String exceptionMsg = "This date is not available";
        when(studentRepository.findByIdAndDeletedFalse(any())).thenReturn(Optional.of(student));
        when(teacherRepository.findByIdAndDeletedFalse(any())).thenReturn(Optional.of(teacher));
        when(lessonRepository.existsByTeacherAndDatetimeGreaterThanAndDatetimeLessThan(any(), any(), any())).thenReturn(true);
        assertThatExceptionOfType(TermUnavailableException.class).isThrownBy(() -> lessonService.save(createLessonCommand)).withMessage(exceptionMsg);
        verify(lessonRepository, never()).save(any());
    }
//    change date
}
