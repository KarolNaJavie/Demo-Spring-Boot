package com.example.demo.service;

import com.example.demo.model.Student;
import com.example.demo.model.Teacher;
import com.example.demo.repository.StudentRepository;
import com.example.demo.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentService {
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;

    public List<Student> findAll(){
        return studentRepository.findAll();
    }

    public void deleteById(long id) {
        studentRepository.deleteById(id);
    }

    public void save(Student student, Long teacherId){
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Teacher not found: " + teacherId));
        student.setTeacher(teacher);
        studentRepository.save(student);
    }

    public Student findById (Long id){
        return studentRepository.findById(id).orElseThrow(() -> new RuntimeException("Student not found: " + id ));
    }
}
