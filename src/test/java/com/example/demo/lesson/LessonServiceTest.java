package com.example.demo.lesson;

import com.example.demo.model.Lesson;
import com.example.demo.repository.LessonRepository;
import com.example.demo.repository.StudentRepository;
import com.example.demo.repository.TeacherRepository;
import com.example.demo.service.LessonService;
import net.bytebuddy.asm.Advice;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
}
