package com.example.demo.service;

import com.example.demo.model.CreateTeacherCommand;
import com.example.demo.model.common.Language;
import com.example.demo.model.Teacher;
import com.example.demo.model.dto.TeacherDTO;
import com.example.demo.repository.TeacherRepository;
import jakarta.persistence.EntityNotFoundException;

import jakarta.validation.Valid;
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
    public List<TeacherDTO> findAllActive() {
        return teacherRepository.findAllByDeletedFalse()
                .stream()
                .map(TeacherDTO::fromEntity)
                .toList();
    }

    @Transactional
    public TeacherDTO save(CreateTeacherCommand createTeacherCommand) {
        return TeacherDTO.fromEntity(teacherRepository.save(createTeacherCommand.toEntity()));
    }

    @Transactional
    public void deleteById(Long id) {
        teacherRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public TeacherDTO findById(Long id) {
        return teacherRepository.findById(id).map(TeacherDTO::fromEntity)
                .orElseThrow(() -> new EntityNotFoundException(MessageFormat.format("Teacher with id={0} not found", id)));
    }

    @Transactional(readOnly = true)
    public List<TeacherDTO> findAllByLanguage(Language language) {
        return teacherRepository.findAllByLanguagesContaining(language)
                .stream()
                .map(TeacherDTO::fromEntity)
                .toList();
    }

    @Transactional
    public void softDelete(Long id) {
        Teacher teacher = teacherRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(MessageFormat.format("Teacher with id={0} not found", id)));
        teacher.setDeleted(true);
    }

    public TeacherDTO update(@Valid CreateTeacherCommand command, Long id) {
        Teacher teacher = teacherRepository.findByIdAndDeletedFalse(id).orElseThrow(() ->
                new EntityNotFoundException(MessageFormat.format("Teacher with id={0} not found", id)));
        teacher.setFirstName(command.getFirstName());
        teacher.setLastName(command.getLastName());
        teacher.setLanguages(command.getLanguages());
        return TeacherDTO.fromEntity(teacherRepository.save(teacher));
    }
}
