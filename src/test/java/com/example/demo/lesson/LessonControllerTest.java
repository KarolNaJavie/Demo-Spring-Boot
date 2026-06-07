package com.example.demo.lesson;

import com.example.demo.model.Lesson;
import com.example.demo.model.Student;
import com.example.demo.model.Teacher;
import com.example.demo.repository.LessonRepository;
import com.example.demo.repository.StudentRepository;
import com.example.demo.repository.TeacherRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;


import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class LessonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Test
    void testFindAll_ResultsInLessonListBeingReturned() throws Exception {

        lessonRepository.save(Lesson.builder()
                .teacher(teacherRepository.save(Teacher.builder().firstName("Adrina").lastName("Test").deleted(false).build()))
                .student(studentRepository.save(Student.builder().firstName("Adrina").lastName("Test").deleted(false).build()))
                .datetime(LocalDateTime.of(2026, 6, 7, 20, 15))
                .build());

        mockMvc.perform(get("/lessons"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void testDelete_ResultsInTeacherDeleted() throws Exception {
        lessonRepository.save(Lesson.builder()
                .teacher(teacherRepository.save(Teacher.builder().firstName("Adrina").lastName("Test").deleted(false).build()))
                .student(studentRepository.save(Student.builder().firstName("Adrina").lastName("Test").deleted(false).build()))
                .datetime(LocalDateTime.of(2026, 6, 7, 20, 15))
                .build());
        mockMvc.perform(delete("/lessons/{id}", 1L))
                .andExpect(status().isNoContent());
    }

    @Test
    void testUpdate_ResultsInChangedDate() throws Exception {
        Lesson lesson = lessonRepository.save(Lesson.builder()
                .teacher(teacherRepository.save(Teacher.builder().firstName("Adrina").lastName("Test").deleted(false).build()))
                .student(studentRepository.save(Student.builder().firstName("Adrina").lastName("Test").deleted(false).build()))
                .datetime(LocalDateTime.of(2026, 6, 7, 20, 15))
                .build());
        LocalDateTime newDate = lesson.getDatetime().plusDays(7);

        mockMvc.perform(put("/lessons/{id}", lesson.getId())
                        .param("newDate", String.valueOf(newDate)))
                .andExpect(jsonPath("$.datetime",
                        is("2026-06-14T20:15:00")));
    }

}
