package com.example.demo.service;

import com.example.demo.model.Teacher;
import com.example.demo.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TeacherService {

    private final TeacherRepository teacherRepository;

    public List<Teacher> findAll() {
        return teacherRepository.findAll();
    }

    public void deleteById(long id) {
        teacherRepository.deleteById(id);
    }

    public void save(Teacher teacher1){
        teacherRepository.save(teacher1);
    }

    public Teacher findById(Long id){
        return teacherRepository.findById(id).orElseThrow(() -> new RuntimeException("Teacher not found: " + id));
    }
}
