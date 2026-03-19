package com.example.demo.lesson;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.example.demo.model.LessonCannotBeInThePastException;
import com.example.demo.model.LessonHasAlreadyStartedException;
import com.example.demo.model.TermUnavailableException;
import com.example.demo.model.Lesson;
import com.example.demo.model.Student;
import com.example.demo.model.Teacher;
import com.example.demo.repository.LessonRepository;
import com.example.demo.repository.StudentRepository;
import com.example.demo.repository.TeacherRepository;
import com.example.demo.service.LessonService;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

// =====================================================================
// CO TO SĄ TESTY JEDNOSTKOWE?
// =====================================================================
// Test jednostkowy (unit test) sprawdza jedną, konkretną "jednostkę" kodu
// w IZOLACJI od reszty systemu - bez bazy danych, bez serwera, bez sieci.
//
// "Jednostka" = zazwyczaj jedna metoda jednej klasy (tutaj: LessonService).
//
// Dlaczego izolacja? Bo chcemy testować TYLKO logikę biznesową serwisu,
// a nie to czy baza danych działa. Dlatego zamiast prawdziwych repozytoriów
// używamy MOCKÓW - podróbek, które zachowują się tak jak im każemy.
//
// Struktura każdego testu to wzorzec GIVEN / WHEN / THEN:
//   GIVEN  - przygotuj dane i skonfiguruj mocki (co zwrócą)
//   WHEN   - wywołaj metodę którą testujesz
//   THEN   - sprawdź czy wynik jest poprawny i czy serwis zachował się prawidłowo
// =====================================================================

// @ExtendWith(MockitoExtension.class) - mówi JUnit 5 żeby użył Mockito
// do obsługi adnotacji @Mock i @InjectMocks w tej klasie testowej
@ExtendWith(MockitoExtension.class)
class LessonServiceTest {

    // @InjectMocks - tworzy PRAWDZIWY obiekt LessonService
    // i automatycznie wstrzykuje do niego wszystkie pola oznaczone @Mock
    // (tak jak Spring wstrzykuje zależności, ale bez Springa!)
    @InjectMocks
    private LessonService lessonService;

    // @Mock - tworzy PODRÓBKĘ (mocka) repozytorium.
    // Mock domyślnie nic nie robi: metody void - nic nie wykonują,
    // metody zwracające wartość - zwracają null / 0 / false / Optional.empty().
    // Możemy mu "zaprogramować" zachowanie przez: when(...).thenReturn(...)
    @Mock
    private LessonRepository lessonRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private TeacherRepository teacherRepository;

    // @Captor - ArgumentCaptor to "pułapka" na argument przekazany do mocka.
    // Gdy serwis woła np. lessonRepository.save(lesson), captor przechwytuje
    // ten obiekt lesson i możemy potem sprawdzić jego pola (assertEquals itp.)
    @Captor
    private ArgumentCaptor<Lesson> lessonArgumentCaptor;

    // Captor na argument Long (id lekcji) do metod deleteById / findById
    @Captor
    private ArgumentCaptor<Long> lessonIdArgumentCaptor;

    // ----------------------------------------------------------------
    // KONWENCJA NAZEWNICTWA TESTÓW:
    // test[NazwaMetody]_[Scenariusz]_ResultsIn[OczekiwanyEfekt]
    //
    // "HappyPath" = ścieżka sukcesu - wszystko idzie zgodnie z planem
    // ----------------------------------------------------------------

    @Test
    void testFindAll_HappyPath_ResultInAllLessonsBeingFound() {
        // GIVEN - przygotowujemy testowe dane (3 puste lekcje)
        // Lesson.builder().build() tworzy obiekt z samymi wartościami domyślnymi
        Lesson lesson1 = Lesson.builder().build();
        Lesson lesson2 = Lesson.builder().build();
        Lesson lesson3 = Lesson.builder().build();
        List<Lesson> lessons = List.of(lesson1, lesson2, lesson3);

        // when(...).thenReturn(...) - "zaprogramowanie" mocka:
        // "kiedy ktoś wywoła findAll(), udawaj że zwróciłeś tę listę"
        when(lessonService.findAll()).thenReturn(lessons);

        // WHEN - wywołujemy testowaną metodę
        List<Lesson> saved = lessonService.findAll();

        // THEN - verify() sprawdza CZY mock został wywołany (i ile razy)
        // Jeśli serwis nie wywołałby lessonRepository.findAll() - test by padł
        verify(lessonRepository).findAll();
        assertEquals(saved, lessons);
    }

    @Test
    void testSave_HappyPath_ResultsInLessonBeingSaved() {
        // GIVEN
        long studentId = 1L;
        long teacherId = 2L;
        LocalDateTime datetime = LocalDateTime.now().plusHours(2); // termin w przyszłości

        // Tworzymy encje testowe - new Student()/Teacher() dają puste obiekty
        Student student = new Student();
        Teacher teacher = new Teacher();

        // Lekcja z terminem w przyszłości - powinna przejść walidację
        Lesson lesson = Lesson.builder()
                .datetime(datetime)
                .build();

        // Obliczamy okno czasowe ±1h wokół daty lekcji
        // Serwis sprawdza czy w tym oknie nauczyciel nie ma już innej lekcji
        LocalDateTime oneHourInTheFuture = lesson.getDatetime().plusHours(1);
        LocalDateTime oneHourInThePast = lesson.getDatetime().minusHours(1);

        // Każde when().thenReturn() to jeden "kontrakt" z mockiem:
        // studentRepository zachowuje się jakby znalazł studenta o id=1
        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));
        when(teacherRepository.findById(teacherId)).thenReturn(Optional.of(teacher));
        // Symulujemy że termin jest WOLNY (false = brak kolizji)
        when(lessonRepository.existsByTeacherAndDatetimeGreaterThanAndDatetimeLessThan(
                teacher, oneHourInThePast, oneHourInTheFuture))
                .thenReturn(false);

        // WHEN
        lessonService.save(lesson, studentId, teacherId);

        // THEN
        // Weryfikujemy że serwis rzeczywiście odpytał repozytoria w odpowiedniej kolejności
        verify(studentRepository).findById(studentId);
        verify(teacherRepository).findById(teacherId);
        verify(lessonRepository).existsByTeacherAndDatetimeGreaterThanAndDatetimeLessThan(
                teacher, oneHourInThePast, oneHourInTheFuture);

        // ArgumentCaptor - przechwytujemy obiekt Lesson który serwis przekazał do save()
        // Dzięki temu możemy sprawdzić CZY serwis prawidłowo ustawił pola przed zapisem
        verify(lessonRepository).save(lessonArgumentCaptor.capture());
        Lesson saved = lessonArgumentCaptor.getValue();
        assertEquals(datetime, saved.getDatetime());    // data musi być niezmieniona
        assertEquals(student, saved.getStudent());      // lekcja musi mieć studenta
        assertEquals(teacher, saved.getTeacher());      // lekcja musi mieć nauczyciela
    }

    // ----------------------------------------------------------------
    // TESTY WYJĄTKÓW - sprawdzamy czy serwis rzuca właściwy wyjątek
    // gdy coś pójdzie nie tak (termin zajęty, lekcja w przeszłości itp.)
    // ----------------------------------------------------------------

    @Test
    void testSave_TermUnavailable_ResultsInTermUnavailableException() {
        // GIVEN
        String exceptionMsg = "This date is no available"; // literówka z oryginału!
        long studentId = 1L;
        long teacherId = 2L;
        LocalDateTime datetime = LocalDateTime.now().plusHours(2);

        Teacher teacher = new Teacher();
        Lesson lesson = Lesson.builder()
                .datetime(datetime)
                .build();
        LocalDateTime oneHourInTheFuture = lesson.getDatetime().plusHours(1);
        LocalDateTime oneHourInThePast = lesson.getDatetime().minusHours(1);

        when(studentRepository.findById(studentId)).thenReturn(Optional.of(new Student()));
        when(teacherRepository.findById(teacherId)).thenReturn(Optional.of(teacher));
        // Tym razem termin jest ZAJĘTY (true = kolizja) - serwis powinien rzucić wyjątek
        when(lessonRepository.existsByTeacherAndDatetimeGreaterThanAndDatetimeLessThan(
                teacher, oneHourInThePast, oneHourInTheFuture))
                .thenReturn(true);

        // assertThatExceptionOfType - sprawdza:
        // 1. Czy wyjątek w ogóle został rzucony
        // 2. Czy jest właściwego TYPU (TermUnavailableException)
        // 3. Czy ma właściwą WIADOMOŚĆ (.withMessage)
        assertThatExceptionOfType(TermUnavailableException.class)
                .isThrownBy(() -> lessonService.save(lesson, studentId, teacherId))
                .withMessage(exceptionMsg);

        verify(studentRepository).findById(studentId);
        verify(teacherRepository).findById(teacherId);
        verify(lessonRepository).existsByTeacherAndDatetimeGreaterThanAndDatetimeLessThan(
                teacher, oneHourInThePast, oneHourInTheFuture);
        // verifyNoMoreInteractions - upewniamy się że po rzuceniu wyjątku
        // serwis NIE wywołał niczego więcej na repozytorium (np. nie zapisał)
        verifyNoMoreInteractions(lessonRepository);
    }

    @Test
    void testSave_TermInThePast_ResultsInLessonCannotBeInThePastException() {
        // GIVEN
        String exceptionMsg = "Lesson cannot be in the past";
        long studentId = 1L;
        long teacherId = 2L;
        // Data w PRZESZŁOŚCI - serwis powinien to odrzucić
        LocalDateTime datetime = LocalDateTime.now().minusHours(2);

        Lesson lesson = Lesson.builder()
                .datetime(datetime)
                .build();

        // WAŻNE: save() najpierw szuka studenta, potem nauczyciela, POTEM waliduje datę
        // Dlatego MUSIMY dostarczyć obu - inaczej EntityNotFoundException zamiast LessonCannotBeInThePastException
        when(studentRepository.findById(studentId)).thenReturn(Optional.of(new Student()));
        when(teacherRepository.findById(teacherId)).thenReturn(Optional.of(new Teacher()));

        // WHEN + THEN
        assertThatExceptionOfType(LessonCannotBeInThePastException.class)
                .isThrownBy(() -> lessonService.save(lesson, studentId, teacherId))
                .withMessage(exceptionMsg);

        verify(studentRepository).findById(studentId);
        verify(teacherRepository).findById(teacherId);
        // verifyNoInteractions - lessonRepository nie powinien być WCALE ruszony,
        // bo walidacja daty powinna nastąpić PRZED sprawdzeniem wolnego terminu
        verifyNoInteractions(lessonRepository);
    }

    @Test
    void testSave_TeacherNotFound_ResultsInEntityNotFoundException() {
        // GIVEN
        long studentId = 1L;
        long teacherId = 2L;
        String exceptionMsg = MessageFormat.format("Teacher with id={0} not found", teacherId);

        // Student istnieje - musimy go znaleźć bo serwis szuka studenta PRZED nauczycielem
        when(studentRepository.findById(studentId)).thenReturn(Optional.of(new Student()));
        // Optional.empty() - symulujemy że nauczyciel o podanym id NIE ISTNIEJE w bazie
        when(teacherRepository.findById(teacherId)).thenReturn(Optional.empty());

        // WHEN + THEN
        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> lessonService.save(new Lesson(), studentId, teacherId))
                .withMessage(exceptionMsg);

        verify(studentRepository).findById(studentId);
        verify(teacherRepository).findById(teacherId);
        // Skoro nie ma nauczyciela - lessonRepository nie powinien być ruszony
        verifyNoInteractions(lessonRepository);
    }

    @Test
    void testSave_StudentNotFound_ResultsInEntityNotFoundException() {
        // GIVEN
        long studentId = 1L;
        long teacherId = 2L;
        String exceptionMsg = MessageFormat.format("Student with id={0} not found", studentId);

        // Optional.empty() - student nie istnieje; serwis szuka studenta PIERWSZEGO
        // więc teacherRepository w ogóle nie zostanie wywołany
        when(studentRepository.findById(studentId)).thenReturn(Optional.empty());

        // WHEN + THEN
        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> lessonService.save(new Lesson(), studentId, teacherId))
                .withMessage(exceptionMsg);

        verify(studentRepository).findById(studentId);
        // teacherRepository i lessonRepository - zero interakcji (student rzucił wcześniej)
        verifyNoInteractions(teacherRepository);
        verifyNoInteractions(lessonRepository);
    }

    @Test
    void testDeleteById_HappyPath_ResultsInLessonBeingDeleted() {
        // GIVEN - lekcja jeszcze nie zaczęta (termin w przyszłości) - można usunąć
        long lessonId = 1L;
        Lesson lesson = Lesson.builder()
                .datetime(LocalDateTime.now().plusHours(10)) // za 10 godzin
                .build();

        when(lessonRepository.findById(lessonId)).thenReturn(Optional.of(lesson));

        // WHEN
        lessonService.deleteById(lessonId);

        // THEN
        verify(lessonRepository).findById(lessonId);
        verify(lessonRepository).deleteById(lessonId);
    }

    @Test
    void testDeleteById_LessonAlreadyStarted_ResultsInLessonHasAlreadyStartedException() {
        // GIVEN - lekcja już się ZACZĘŁA (termin w przeszłości) - nie można usunąć
        long lessonId = 1L;
        String exceptionMsg = "Lesson has already started";
        Lesson lesson = Lesson.builder()
                .id(lessonId)
                .datetime(LocalDateTime.now().minusHours(1)) // godzinę temu!
                .build();

        when(lessonRepository.findById(lessonId)).thenReturn(Optional.of(lesson));

        // WHEN + THEN
        assertThatExceptionOfType(LessonHasAlreadyStartedException.class)
                .isThrownBy(() -> lessonService.deleteById(lessonId))
                .withMessage(exceptionMsg);

        verify(lessonRepository).findById(lessonId);
        // deleteById repozytorium NIE powinno być wywołane
        verifyNoMoreInteractions(lessonRepository);
    }

    @Test
    void testDeleteById_LessonNotFound_ResultsInEntityNotFoundException() {
        // GIVEN - lekcja o tym id nie istnieje w bazie
        Long lessonId = 1L;
        String exceptionMsg = MessageFormat.format("Lesson with id={0} not found", lessonId);

        // Optional.empty() - baza "nie znalazła" lekcji
        when(lessonRepository.findById(lessonId)).thenReturn(Optional.empty());

        // WHEN + THEN
        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> lessonService.deleteById(lessonId))
                .withMessage(exceptionMsg);

        verify(lessonRepository).findById(lessonId);
        verifyNoMoreInteractions(lessonRepository);
    }

    @Test
    void testChangeDate_HappyPath_ResultsInLessonWithChangedDate() {
        // GIVEN
        long lessonId = 1L;
        LocalDateTime newDatetime = LocalDateTime.now().plusHours(2); // nowy termin w przyszłości
        LocalDateTime oneHourInTheFuture = newDatetime.plusHours(1);
        LocalDateTime oneHourInThePast = newDatetime.minusHours(1);

        // Stary termin lekcji musi być też w przyszłości (lekcja jeszcze się nie zaczęła)
        Lesson lesson = Lesson.builder()
                .datetime(LocalDateTime.now().plusHours(1))
                .id(lessonId)
                .build();

        when(lessonRepository.findById(lessonId)).thenReturn(Optional.of(lesson));
        // Nowy termin wolny (false = brak kolizji)
        when(lessonRepository.existsByTeacherAndDatetimeGreaterThanAndDatetimeLessThan(
                lesson.getTeacher(), oneHourInThePast, oneHourInTheFuture))
                .thenReturn(false);

        // WHEN + THEN (nie rzuca wyjątku = sukces; @Transactional bez Spring = brak save)
        lessonService.changeDate(lessonId, newDatetime);
    }

    @Test
    void testChangeDate_LessonInThePast_ResultsInLessonCannotBeInThePastException() {
        // GIVEN - nowy termin jest w PRZESZŁOŚCI
        long lessonId = 1L;
        LocalDateTime newDateTime = LocalDateTime.now().minusHours(2); // 2 godziny temu!
        String exceptionMsg = "Lesson cannot be in the past";

        // WAŻNE: changeDate() sprawdza datę NA POCZĄTKU, PRZED wywołaniem findById()
        // Dlatego NIE konfigurujemy żadnego mocka dla lessonRepository

        // WHEN + THEN
        assertThatExceptionOfType(LessonCannotBeInThePastException.class)
                .isThrownBy(() -> lessonService.changeDate(lessonId, newDateTime))
                .withMessage(exceptionMsg);

        // Wyjątek rzucony przed jakimkolwiek odpytaniem repozytorium
        verifyNoMoreInteractions(lessonRepository);
    }

    @Test
    void testChangeDate_LessonNotFound_ResultsInEntityNotFoundException() {
        // GIVEN
        long lessonId = 1L;
        LocalDateTime newDateTime = LocalDateTime.now().plusHours(10);
        String exceptionMsg = MessageFormat.format("Lesson with id={0} not found", lessonId);

        // Lekcja o tym id nie istnieje
        // (nie konfigurujemy mocka = domyślnie Optional.empty())

        // WHEN + THEN
        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> lessonService.changeDate(lessonId, newDateTime))
                .withMessage(exceptionMsg);

        verify(lessonRepository).findById(lessonId);
        verifyNoMoreInteractions(lessonRepository);
    }

    @Test
    void testChangeDate_TermUnavailable_ResultsInTermUnavailableException() {
        // GIVEN - nowy termin koliduje z inną lekcją tego samego nauczyciela
        String exceptionMsg = "This date is no available"; // literówka z oryginału!
        long lessonId = 2L;
        LocalDateTime datetime = LocalDateTime.now().plusHours(2);

        // Nauczyciel przypisany do lekcji (będzie użyty w sprawdzeniu kolizji)
        Teacher teacher = new Teacher();
        Lesson lesson = Lesson.builder()
                .datetime(datetime) // obecna data = nowa data (dla uproszczenia testu)
                .teacher(teacher)
                .build();
        // Okno czasowe wokół nowego terminu
        LocalDateTime oneHourInTheFuture = lesson.getDatetime().plusHours(1);
        LocalDateTime oneHourInThePast = lesson.getDatetime().minusHours(1);

        when(lessonRepository.findById(lessonId)).thenReturn(Optional.of(lesson));
        // true = kolizja! nauczyciel jest zajęty w tym czasie
        when(lessonRepository.existsByTeacherAndDatetimeGreaterThanAndDatetimeLessThan(
                teacher, oneHourInThePast, oneHourInTheFuture))
                .thenReturn(true);

        // WHEN + THEN
        assertThatExceptionOfType(TermUnavailableException.class)
                .isThrownBy(() -> lessonService.changeDate(lessonId, datetime))
                .withMessage(exceptionMsg);

        verify(lessonRepository).existsByTeacherAndDatetimeGreaterThanAndDatetimeLessThan(
                teacher, oneHourInThePast, oneHourInTheFuture);
        verifyNoMoreInteractions(lessonRepository);
    }

    @Test
    void testFindLessonById_HappyPath_ResultsInLessonBeingFound() {
        // GIVEN - lekcja istnieje w bazie
        long lessonId = 1L;
        Lesson lesson = Lesson.builder()
                .id(lessonId)
                .build();

        // Ten mock obsługuje OBA wywołania findById (pierwsze w when, drugie w teście)
        when(lessonRepository.findById(lessonId)).thenReturn(Optional.of(lesson));

        // WHEN - pierwsze wywołanie tylko weryfikuje brak wyjątku
        lessonService.findById(lessonId);

        verify(lessonRepository).findById(lessonId);

        // Drugie wywołanie weryfikuje poprawność zwróconego obiektu
        Lesson foundLesson = lessonService.findById(1L);
        assertNotNull(foundLesson);                 // nie null
        assertEquals(lessonId, foundLesson.getId()); // id zgodne z oczekiwanym
    }

    @Test
    void testFindById_WrongPath_ResultInEntityNotFoundException() {
        // anyLong() - matcher Mockito oznaczający "dowolny long"
        // Gdy findById jest wywołane z JAKIMKOLWIEK long - zwróć Optional.empty()
        when(lessonRepository.findById(anyLong())).thenReturn(Optional.empty());

        // assertThrows - alternatywna składnia do assertThatExceptionOfType
        // Sprawdza tylko TYP wyjątku, nie wiadomość
        assertThrows(EntityNotFoundException.class, () -> lessonService.findById(anyLong()));
        verify(lessonRepository).findById(anyLong());
        verifyNoMoreInteractions(lessonRepository);
    }
}