package com.example.demo.service;

import com.example.demo.model.*;
import com.example.demo.model.common.exception.LanguageMismatch;
import com.example.demo.model.common.exception.LessonCannotBeInThePastException;
import com.example.demo.model.common.exception.LessonHasAlreadyStartedException;
import com.example.demo.model.common.exception.TermUnavailableException;
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

    public List<Lesson> findAll() {
        return lessonRepository.findAll();
    }

    @Transactional
    public void save(Lesson lesson, long studentId, long teacherId) {
        Student student = studentRepository.findById(studentId).orElseThrow(() -> new EntityNotFoundException(MessageFormat.format("Student with id={0} not found", studentId)));
        Teacher teacher = teacherRepository.findById(teacherId).orElseThrow(() -> new EntityNotFoundException(MessageFormat.format("Teacher with id={0} not found", teacherId)));
        if (lesson.getDatetime().isBefore(LocalDateTime.now())) {
            throw new LessonCannotBeInThePastException();
        }
        LocalDateTime from = lesson.getDatetime().minusHours(1);
        LocalDateTime to = lesson.getDatetime().plusHours(1);
        if (lessonRepository.existsByTeacherAndDatetimeGreaterThanAndDatetimeLessThan(teacher, from, to)) {
            throw new TermUnavailableException();
        }
        if (!teacher.getLanguages().contains(student.getLanguage())) {
            throw new LanguageMismatch();
        }
        lesson.setStudent(student);
        lesson.setTeacher(teacher);
        lessonRepository.save(lesson);
    }

    @Transactional
    public void changeDate(long lessonId, LocalDateTime dateTime) {
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
