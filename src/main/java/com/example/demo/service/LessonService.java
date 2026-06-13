package com.example.demo.service;

import com.example.demo.model.*;
import com.example.demo.model.common.exception.LanguageMismatch;
import com.example.demo.model.common.exception.LessonCannotBeInThePastException;
import com.example.demo.model.common.exception.LessonHasAlreadyStartedException;
import com.example.demo.model.common.exception.TermUnavailableException;
import com.example.demo.model.dto.LessonDTO;
import com.example.demo.repository.LessonRepository;
import com.example.demo.repository.StudentRepository;
import com.example.demo.repository.TeacherRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LessonService {

    private final LessonRepository lessonRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;

    public List<LessonDTO> findAll() {

        return lessonRepository.findAll()
                .stream()
                .map(LessonDTO::fromEntity)
                .toList();
    }

    @Transactional
    public LessonDTO save(CreateLessonCommand createLessonCommand) {
        Student student = studentRepository.findByIdAndDeletedFalse(createLessonCommand.getStudentId()).orElseThrow(() -> new EntityNotFoundException(MessageFormat.format("Student with id={0} not found", createLessonCommand.getStudentId())));
        Teacher teacher = teacherRepository.findByIdAndDeletedFalse(createLessonCommand.getTeacherId()).orElseThrow(() -> new EntityNotFoundException(MessageFormat.format("Teacher with id={0} not found", createLessonCommand.getTeacherId())));
        if (createLessonCommand.getDatetime().isBefore(LocalDateTime.now())) {
            throw new LessonCannotBeInThePastException();
        }
        LocalDateTime from = createLessonCommand.getDatetime().minusHours(1);
        LocalDateTime to = createLessonCommand.getDatetime().plusHours(1);
        if (lessonRepository.existsByTeacherAndDatetimeGreaterThanAndDatetimeLessThan(teacher, from, to)) {
            throw new TermUnavailableException();
        }
        if (!teacher.getLanguages().contains(student.getLanguage())) {
            throw new LanguageMismatch();
        }
        Lesson lesson = Lesson
                .builder()
                .datetime(createLessonCommand.getDatetime())
                .teacher(teacher)
                .student(student)
                .build();
        return LessonDTO.fromEntity(lessonRepository.save(lesson));
    }

    @Transactional
    public LessonDTO changeDate(long lessonId, LocalDateTime dateTime) {
        if (dateTime.isBefore(LocalDateTime.now())) {
            throw new LessonCannotBeInThePastException();
        }
        Lesson lesson = lessonRepository.findById(lessonId).orElseThrow(() -> new EntityNotFoundException(MessageFormat.format("Lesson with id={0} not found", lessonId)));
        Teacher teacher = lesson.getTeacher();
        LocalDateTime from = dateTime.minusHours(1);
        LocalDateTime to = dateTime.plusHours(1);

        if (lessonRepository.existsByTeacherAndDatetimeGreaterThanAndDatetimeLessThanAndIdNot(teacher, from, to, lesson.getId())) {
            throw new TermUnavailableException();
        }
        lesson.setDatetime(dateTime);
//        lessonRepository.save(lesson); //docelowo niepotrzbne przy transactional
        return LessonDTO.fromEntity(lesson);
    }

    @Transactional
    public void deleteById(long lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId).orElseThrow(() -> new EntityNotFoundException(MessageFormat.format("Lesson with id={0} not found", lessonId)));
        if (lesson.getDatetime().isBefore(LocalDateTime.now())) {
            throw new LessonHasAlreadyStartedException();
        }
        lessonRepository.deleteById(lessonId);
    }

    @Transactional(readOnly = true)
    public Lesson findById(long id) {
        return lessonRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(MessageFormat.format("Lesson with id={0} not found", id)));
    }
}
