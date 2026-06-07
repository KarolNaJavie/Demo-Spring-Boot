package com.example.demo.student;

import com.example.demo.model.CreateStudentCommand;
import com.example.demo.model.CreateTeacherCommand;
import com.example.demo.model.Student;
import com.example.demo.model.Teacher;
import com.example.demo.model.common.Language;
import com.example.demo.repository.StudentRepository;
import com.example.demo.repository.TeacherRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.function.RequestPredicates.contentType;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class StudentControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private TeacherRepository teacherRepository;


    @Test
    void testFindAll_ResultsInStudentsListBeingReturned() throws Exception {
        studentRepository.saveAll(List.of(
                Student.builder().firstName("Adrina").lastName("Test").deleted(false).build(),
                Student.builder().firstName("Karolina").lastName("Test").deleted(false).build()
        ));

        mockMvc.perform(get("/students"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void testCreate_ResultsInStudentBeingCreated() throws Exception {
        Teacher teacher = teacherRepository.save(Teacher.builder().firstName("Adrina").lastName("Test").deleted(false).languages(Set.of(Language.JAVA)).build());
        CreateStudentCommand command = new CreateStudentCommand();
        command.setFirstName("Jacek");
        command.setLastName("Jacek");
        command.setLanguage(Language.JAVA);
        command.setTeacherId(teacher.getId());
        String value = objectMapper.writeValueAsString(command);
        mockMvc.perform(post("/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(value))
                .andDo(print())
                .andExpect(jsonPath("$.firstName", is("Jacek")))
                .andExpect(jsonPath("$.lastName", is("Jacek")));
    }

    @Test
    void testDelete_ResultsInStudentBeingDeleted() throws Exception {
        Teacher teacher = teacherRepository.save(Teacher.builder().firstName("Adrina").lastName("Test").deleted(false).languages(Set.of(Language.JAVA)).build());
        studentRepository.save(Student.builder().teacher(teacher).language(Language.JAVA).build());

        mockMvc.perform(delete("/teachers/{id}", 1L))
                .andExpect(status().isNoContent());
    }

    @Test
    void testUpdate_ResultsInStudentBeingUpdated() throws Exception {
        Teacher teacher = teacherRepository.save(Teacher.builder().firstName("Adrina").lastName("Test").deleted(false).languages(Set.of(Language.JAVA)).build());
        studentRepository.save(Student.builder().teacher(teacher).language(Language.JAVA).build());
        CreateStudentCommand command = new CreateStudentCommand();
        command.setFirstName("Jacek");
        command.setLastName("Jacek");
        command.setLanguage(Language.JAVA);
        command.setTeacherId(teacher.getId());
        String value = objectMapper.writeValueAsString(command);

        mockMvc.perform(put("/students/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(value))
                .andDo(print())
                .andExpect(jsonPath("$.firstName", is("Jacek")))
                .andExpect(jsonPath("$.lastName", is("Jacek")));
    }
}
