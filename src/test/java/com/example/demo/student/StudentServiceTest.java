package com.example.demo.student;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.example.demo.model.Language;
import com.example.demo.model.LanguageMismatchException;
import com.example.demo.model.Student;
import com.example.demo.model.Teacher;
import com.example.demo.repository.StudentRepository;
import com.example.demo.repository.TeacherRepository;
import com.example.demo.service.StudentService;

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

// =====================================================================
// TESTY JEDNOSTKOWE DLA StudentService
// =====================================================================
// StudentService zależy od dwóch repozytoriów:
//   - StudentRepository  (operacje CRUD na studentach)
//   - TeacherRepository  (potrzebne przy przypisywaniu nauczyciela)
// Oba są mockowane - żadna baza danych nie jest używana.
// =====================================================================
@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    // @InjectMocks - prawdziwy serwis, Mockito wstrzyknie do niego mocki
    @InjectMocks
    private StudentService studentService;

    // @Mock - podróbka repozytorium studentów
    @Mock
    private StudentRepository studentRepository;

    // @Mock - podróbka repozytorium nauczycieli
    @Mock
    private TeacherRepository teacherRepository;

    // Captor - przechwytuje obiekt Student przekazany do save()
    // pozwala sprawdzić czy serwis ustawił właściwe pola przed zapisem
    @Captor
    private ArgumentCaptor<Student> studentArgumentCaptor;

    // Captor - przechwytuje Long id przekazany do deleteById()
    @Captor
    private ArgumentCaptor<Long> studentLongArgumentCaptor;

    // Captor - przechwytuje Teacher przekazany do findAllByTeacher()
    @Captor
    private ArgumentCaptor<Teacher> studentTeacherArgumentCaptor;

    @Test
    void testFindAllByTeacher_HappyPath_ResultInAllMatchingStudentsBeingReturn() {
        // GIVEN - nauczyciel i jego lista studentów
        Teacher teacher = Teacher.builder().build();
        Student student1 = Student.builder().build();
        Student student2 = Student.builder().build();
        Student student3 = Student.builder().build();
        List<Student> students = List.of(student1, student2, student3);

        // Mock zwróci studentów dla konkretnego nauczyciela
        when(studentRepository.findAllByTeacher(teacher)).thenReturn(students);

        // WHEN
        studentService.findAllByTeacher(teacher);

        // THEN - dwa verify: raz bez captora (sprawdza wywołanie), raz z captorem (sprawdza argument)
        verify(studentRepository).findAllByTeacher(teacher);
        verify(studentRepository).findAllByTeacher(studentTeacherArgumentCaptor.capture());
        // Captor sprawdza czy serwis nie podmieniło argumentu po drodze
        assertEquals(teacher, studentTeacherArgumentCaptor.getValue());
    }

    @Test
    void studentDeleteById_HappyPath_ResultStudentBeingDeleted() {
        // GIVEN - id studenta do usunięcia
        long studentId = 1L;

        // WHEN - dla metod void nie konfigurujemy mocka (domyślnie nic nie robi)
        studentService.deleteById(studentId);

        // THEN - deleteById musi zostać wywołane z właściwym id
        verify(studentRepository).deleteById(studentId);
        // verifyNoMoreInteractions - serwis nie powinien robić nic poza deleteById
        verifyNoMoreInteractions(studentRepository);
    }

    @Test
    void testFindAll_HappyPath_ResultInAllStudentsBeingFound() {
        // GIVEN
        Student student1 = Student.builder().build();
        Student student2 = Student.builder().build();
        Student student3 = Student.builder().build();

        List<Student> students = List.of(student1, student2, student3);

        // Programujemy mocka - findAll() ma zwrócić naszą listę
        when(studentService.findAll()).thenReturn(students);

        // WHEN
        List<Student> saved = studentService.findAll();

        // THEN
        verify(studentRepository).findAll();
        assertEquals(saved, students);
    }

    @Test
    void testFindById_HappyPath_ResultStudentBeingFound() {
        // GIVEN
        long studentId = 1L;
        Student student = Student.builder().build();

        // Optional.of(student) - symulujemy że baza "znalazła" studenta
        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));

        // WHEN
        studentService.findById(studentId);

        // THEN
        verify(studentRepository).findById(studentId);
    }

    @Test
    void testFindById_StudentNotFound_ResultsInEntityNotFoundException() {
        // GIVEN
        long studentId = 1L;
        String exceptionMsg = MessageFormat.format("Student with id={0} not found", studentId);

        // Nie konfigurujemy mocka - domyślnie Optional.empty()
        // Serwis powinien złapać empty i rzucić EntityNotFoundException

        // WHEN + THEN
        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> studentService.findById(studentId))
                .withMessage(exceptionMsg);

        verify(studentRepository).findById(studentId);
        verifyNoMoreInteractions(studentRepository);
    }

    @Test
    void testSave_HappyPath_StudentSaved() {
        // GIVEN
        Long teacherId = 1L;
        Student student = new Student();
        Teacher teacher = new Teacher();

        // Nauczyciel istnieje
        when(teacherRepository.findById(teacherId)).thenReturn(Optional.of(teacher));

        // WHEN
        studentService.save(student, teacherId);

        // THEN
        verify(teacherRepository).findById(teacherId);
        verify(studentRepository).save(student);

        // Captor sprawdza czy serwis PRZED zapisem ustawił nauczyciela na studencie
        verify(studentRepository).save(studentArgumentCaptor.capture());
        Student saved = studentArgumentCaptor.getValue();
        assertEquals(student, saved);
        assertEquals(teacher, saved.getTeacher()); // kluczowe: student musi mieć nauczyciela!
    }

    @Test
    void testSave_TeacherNotFound_ResultsInEntityNotFoundException() {
        // GIVEN
        Long teacherId = 1L;
        Student student = new Student();
        String exceptionMsg = MessageFormat.format("Teacher with id={0} not found", teacherId);

        // Nauczyciel nie istnieje - Optional.empty()
        when(teacherRepository.findById(teacherId)).thenReturn(Optional.empty());

        // WHEN + THEN
        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> studentService.save(student, teacherId))
                .withMessage(exceptionMsg);

        verify(teacherRepository).findById(teacherId);
        verifyNoMoreInteractions(teacherRepository);
        // Student NIE powinien zostać zapisany gdy nie ma nauczyciela
        verifyNoMoreInteractions(studentRepository);
    }

    @Test
    void testUpdateTeacher_HappyPath_TeacherUpdated() {
        // GIVEN - student uczy się JAVA, nowy nauczyciel też zna JAVA - zmiana możliwa
        long studentId = 1L;
        long teacherId = 2L;
        Set<Language> teacherLanguages = Set.of(Language.JAVA);

        Student student = Student.builder()
                .language(Language.JAVA) // student chce uczyć się JAVA
                .build();
        Teacher teacher = Teacher.builder()
                .languages(teacherLanguages) // nauczyciel zna JAVA
                .build();

        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));
        when(teacherRepository.findById(teacherId)).thenReturn(Optional.of(teacher));

        // WHEN
        studentService.updateTeacher(studentId, teacherId);

        // THEN - sprawdzamy że student jest zapisany z nowym nauczycielem
        verify(studentRepository).save(student);
        verify(studentRepository).save(studentArgumentCaptor.capture());

        Student saved = studentArgumentCaptor.getValue();
        assertEquals(student, saved);
        assertEquals(teacher, saved.getTeacher()); // nowy nauczyciel musi być ustawiony
    }

    @Test
    void testUpdate_studentNotFound_ResultsInEntityNotFoundException() {
        // GIVEN
        long teacherId = 2L;
        long studentId = 1L;
        String exceptionMsg = MessageFormat.format("Student with id={0} not found", studentId);

        // Student nie istnieje (domyślnie Optional.empty())
        when(studentRepository.findById(studentId)).thenReturn(Optional.empty());

        // WHEN + THEN
        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> studentService.updateTeacher(studentId, teacherId))
                .withMessage(exceptionMsg);

        verify(studentRepository).findById(studentId);
        verifyNoMoreInteractions(studentRepository);
    }

    @Test
    void testUpdate_teacherNotFound_ResultsInEntityNotFoundException() {
        // GIVEN
        long teacherId = 2L;
        long studentId = 1L;
        String exceptionMsg = MessageFormat.format("Teacher with id={0} not found", teacherId);

        // Student istnieje, ale nauczyciel nie
        when(studentRepository.findById(studentId)).thenReturn(Optional.of(new Student()));
        when(teacherRepository.findById(teacherId)).thenReturn(Optional.empty());

        // WHEN + THEN
        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> studentService.updateTeacher(studentId, teacherId))
                .withMessage(exceptionMsg);

        verify(teacherRepository).findById(teacherId);
        verifyNoMoreInteractions(teacherRepository);
    }

    @Test
    void testDeleteById_HappyPath_ResultStudentBeingDeleted() {
        // GIVEN
        long studentId = 1L;

        // UWAGA: Ten test wywołuje BEZPOŚREDNIO na mocku (nie przez serwis)
        // żeby zweryfikować ArgumentCaptor - to technika testowania samego captora
        studentRepository.deleteById(studentId);

        // THEN - verify że mock zarejestrował wywołanie
        verify(studentRepository).deleteById(studentId);

        // Captor przechwytuje id przekazane do deleteById
        verify(studentRepository).deleteById(studentLongArgumentCaptor.capture());
        long deleted = studentLongArgumentCaptor.getValue();
        assertEquals(studentId, deleted); // id musi być niezmienione
    }

    @Test
    void testUpdateTeacher_LanguageNotMatch_LanguageMismatchException() {
        // GIVEN - student uczy się CPP, nauczyciel zna tylko JAVA - NIEZGODNOŚĆ!
        long studentId = 1L;
        long teacherId = 2L;
        String exceptionMsg = "Language does not match teachers language set";

        Set<Language> teacherLanguages = Set.of(Language.JAVA);
        Student student = Student.builder()
                .language(Language.CPP) // CPP ≠ JAVA -> błąd!
                .build();
        Teacher teacher = Teacher.builder()
                .languages(teacherLanguages) // tylko JAVA
                .build();

        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));
        when(teacherRepository.findById(teacherId)).thenReturn(Optional.of(teacher));

        // WHEN + THEN
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> studentService.updateTeacher(studentId, teacherId))
                .withMessage(exceptionMsg);

        // THEN - oba repozytoria musiały zostać odpytane (sprawdzamy po fakcie)
        verify(studentRepository).findById(studentId);
        verify(teacherRepository).findById(teacherId);
    }
}