package com.example.demo.service;

import com.example.demo.model.Student;
import com.example.demo.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentService {
    private final StudentRepository studentRepository;

    public List<Student> findAll(){
        return studentRepository.findAll();
    }

    public void deleteById(long id) {
        studentRepository.deleteById(id);
    }

    public void save(Student student){
        studentRepository.save(student);
    }

    public Student findById (Long id){
        return studentRepository.findById(id).orElseThrow(() -> new RuntimeException("Student not found: " + id ));
    }
}
