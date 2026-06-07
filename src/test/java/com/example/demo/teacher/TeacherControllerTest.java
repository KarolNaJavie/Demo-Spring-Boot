package com.example.demo.teacher;

import com.example.demo.model.CreateTeacherCommand;
import com.example.demo.model.Teacher;
import com.example.demo.model.common.Language;
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

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class TeacherControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TeacherRepository teacherRepository;

    @Test
    void testFindAll_ResultsInTeacherListBeingReturned() throws Exception {
        teacherRepository.saveAll(List.of(
                Teacher.builder().firstName("Adrina").lastName("Test").deleted(false).build(),
                Teacher.builder().firstName("Karolina").lastName("Test").deleted(false).build()
        ));

        mockMvc.perform(get("/teachers"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void testCreate_ResultsInTeacherBeingCreated() throws Exception {
        CreateTeacherCommand command = new CreateTeacherCommand();
        command.setFirstName("Tomek");
        command.setLastName("Ziomek");
        command.setLanguages(Set.of(Language.C, Language.JAVA));
        String requestBody = objectMapper.writeValueAsString(command);

        mockMvc.perform(post("/teachers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(jsonPath("$.firstName", is("Tomek")))
                .andExpect(jsonPath("$.lastName", is("Ziomek")));
    }

    @Test
    void testUpdate_ResultsInTeacherBeingUpdated() throws Exception {
        CreateTeacherCommand command = new CreateTeacherCommand();
        command.setFirstName("Jan");
        command.setLastName("Stonoga");
        command.setLanguages(Set.of(Language.C));
        String body = objectMapper.writeValueAsString(command);
        Teacher teacher = teacherRepository.save(Teacher.builder()
                .firstName(command.getFirstName())
                .lastName("Biedronka")
                .languages(command.getLanguages())
                .build());

        mockMvc.perform(put("/teachers/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(print())
                .andExpect(jsonPath("$.firstName", is("Jan")))
                .andExpect(jsonPath("$.lastName", is("Biedronka")));
        mockMvc.perform(get("/teachers"))
                .andExpect(status().isOk());
//                .andExpect(jsonPath("$", contains(body)));
    }

    @Test
    void testDelete_ResultsInTeacherDeleted() throws Exception {
        teacherRepository.save(Teacher.builder().firstName("Adrina").lastName("Test").deleted(false).build());

        mockMvc.perform(delete("/teachers/{id}", 1L))
                .andExpect(status().isNoContent());

    }

    // dokonczyc testy dla wszystkich endpointow
}
