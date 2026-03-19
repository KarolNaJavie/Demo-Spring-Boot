package com.example.demo.teacher;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.example.demo.model.Language;
import com.example.demo.model.Teacher;
import com.example.demo.repository.TeacherRepository;
import com.example.demo.service.TeacherService;

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
// CO TESTUJEMY W TEJ KLASIE?
// =====================================================================
// Testujemy TeacherService w izolacji – bez bazy danych, bez Springa.
// TeacherService zależy tylko od jednego repozytorium (TeacherRepository),
// więc struktura jest prostsza niż w LessonServiceTest.
//
// Każdy test sprawdza JEDNĄ konkretną ścieżkę wykonania metody:
//   - happy path  → wszystko idzie zgodnie z planem
//   - wrong path  → coś się nie zgadza (brak encji, zły argument itp.)
// =====================================================================
@ExtendWith(MockitoExtension.class)
class TeacherServiceTest {

    // @InjectMocks – tworzy PRAWDZIWY obiekt TeacherService
    // i automatycznie wstrzykuje do niego wszystkie pola oznaczone @Mock
    @InjectMocks
    private TeacherService teacherService;

    // @Mock – tworzy PODRÓBKĘ repozytorium.
    // Nie odpytuje bazy – odpowiada tylko to co mu każemy przez when().thenReturn()
    @Mock
    private TeacherRepository teacherRepository;

    // @Captor – "pułapka" na argument przekazany do mocka.
    // Gdy serwis woła teacherRepository.save(teacher), captor przechwytuje
    // ten obiekt i możemy potem sprawdzić jego pola
    @Captor
    private ArgumentCaptor<Teacher> teacherArgumentCaptor;

    // Captor na argument Language przekazany do findAllByLanguagesContaining()
    @Captor
    private ArgumentCaptor<Language> teacherLanguageArgumentCaptor;

    @Test
    void testFindAll_HappyPath_ResultInAllTeachersBeingFound() {
        // GIVEN – tworzymy 3 puste obiekty Teacher jako dane testowe
        // Teacher.builder().build() tworzy obiekt z samymi nullami/zerami – wystarczy nam do testu
        Teacher teacher1 = Teacher.builder().build();
        Teacher teacher2 = Teacher.builder().build();
        Teacher teacher3 = Teacher.builder().build();

        List<Teacher> teachers = List.of(teacher1, teacher2, teacher3);

        // when(...).thenReturn(...) – "zaprogramowanie" mocka:
        // "kiedy ktoś wywoła findAll() na serwisie, udawaj że repozytorium zwróciło tę listę"
        when(teacherService.findAll()).thenReturn(teachers);

        // WHEN – wywołujemy testowaną metodę
        List<Teacher> saved = teacherService.findAll();

        // THEN – verify() sprawdza CZY mock został wywołany (i ile razy)
        // Jeśli serwis nie wywołałby teacherRepository.findAll() – test by padł
        verify(teacherRepository).findAll();
        assertEquals(saved, teachers);
    }

    @Test
    void testFindAllByLanguage_HappyPath_ResultInAllMatchingTeachersBeingReturn() {
        // GIVEN – nauczyciel zna język JAVA
        Language language = Language.JAVA;
        Set<Language> teacherLanguages = Set.of(language);
        Teacher teacher = Teacher.builder()
                .languages(teacherLanguages)
                .build();

        // Mock zwróci listę z jednym nauczycielem gdy zapytamy o JAVA
        when(teacherRepository.findAllByLanguagesContaining(language)).thenReturn(List.of(teacher));

        // WHEN
        teacherService.findAllByLanguage(language);

        // THEN – weryfikujemy że serwis wywołał metodę z właściwym argumentem
        verify(teacherRepository).findAllByLanguagesContaining(language);

        // Captor przechwytuje argument Language przekazany do repozytorium
        // i pozwala sprawdzić czy serwis nie podmienił go po drodze
        verify(teacherRepository).findAllByLanguagesContaining(teacherLanguageArgumentCaptor.capture());
        assertEquals(language, teacherLanguageArgumentCaptor.getValue());
    }

    @Test
    void testSave_HappyPath_TeacherSaved() {
        // GIVEN
        Teacher teacher = new Teacher();

        // WHEN
        teacherService.save(teacher);

        // THEN
        // Pierwszy verify – sprawdza sam fakt wywołania save() z właściwym obiektem
        verify(teacherRepository).save(teacher);

        // Drugi verify + captor – przechwytujemy obiekt i możemy dokładnie
        // zweryfikować jego stan (tutaj: czy serwis nie zmodyfikował nauczyciela przed zapisem)
        verify(teacherRepository).save(teacherArgumentCaptor.capture());
        Teacher captured = teacherArgumentCaptor.getValue();
        assertEquals(teacher, captured);
    }

    @Test
    void teacherDeleteById_HappyPath_ResultTeacherBeingDeleted() {
        long teacherId = 1L;

        // WHEN – dla operacji void nie konfigurujemy mocka (domyślnie nic nie robi)
        teacherService.deleteById(teacherId);

        // THEN – sprawdzamy że repozytorium zostało wywołane z właściwym id
        verify(teacherRepository).deleteById(teacherId);

        // verifyNoMoreInteractions – upewniamy się że serwis nie wywołał
        // niczego innego poza deleteById (np. nie robił niepotrzebnego findById)
        verifyNoMoreInteractions(teacherRepository);
    }

    @Test
    void testFindById_HappyPath_ResultTeacherBeingFound() {
        long teacherId = 1L;
        Teacher teacher = Teacher.builder().build();

        // Optional.of(teacher) – symulujemy że baza "znalazła" nauczyciela
        when(teacherRepository.findById(teacherId)).thenReturn(Optional.of(teacher));

        // WHEN
        teacherService.findById(teacherId);

        // THEN – repozytorium musiało zostać odpytane dokładnie raz
        verify(teacherRepository).findById(teacherId);
    }

    @Test
    void testFindById_TeacherNotFound_ResultInEntityNotFoundException() {
        long teacherId = 1L;
        // MessageFormat.format() – tak samo tworzymy wiadomość jak w serwisie,
        // żeby porównanie String było dokładnie takie samo
        String exceptionMsg = MessageFormat.format("Teacher with id={0} not found", teacherId);

        // Optional.empty() – symulujemy że baza NIE znalazła nauczyciela o tym id
        when(teacherRepository.findById(teacherId)).thenReturn(Optional.empty());

        // assertThatExceptionOfType – sprawdza:
        // 1. Czy wyjątek w ogóle został rzucony
        // 2. Czy jest właściwego TYPU (EntityNotFoundException)
        // 3. Czy ma właściwą WIADOMOŚĆ (.withMessage)
        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> teacherService.findById(teacherId))
                .withMessage(exceptionMsg);

        verify(teacherRepository).findById(teacherId);
        // Po rzuceniu wyjątku serwis nie powinien robić już nic więcej
        verifyNoMoreInteractions(teacherRepository);
    }
}
