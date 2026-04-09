package com.example.demo.service;

import com.example.demo.model.common.Language;
import com.example.demo.model.Teacher;
import com.example.demo.repository.TeacherRepository;
import jakarta.persistence.EntityNotFoundException;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TeacherService {

    private final TeacherRepository teacherRepository;

    @Transactional(readOnly = true)
    public List<Teacher> findAll() {
        return teacherRepository.findAll();
    }

    @Transactional
    public void save(Teacher teacher) {
        teacherRepository.save(teacher);
    }

    @Transactional
    public void deleteById(Long id) {
        teacherRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Teacher findById(Long id) {
        return teacherRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(MessageFormat.format("Teacher with id={0} not found", id)));
    }

    @Transactional(readOnly = true)
    public List<Teacher> findAllByLanguage(Language language) {
        return teacherRepository.findAllByLanguagesContaining(language);
    }
}
