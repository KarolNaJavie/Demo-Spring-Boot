package com.example.demo.model;

import java.time.LocalDate;

public class Lesson {
    int id;
    Student student;
    Teacher teacher;
    LocalDate termin;

    public Lesson(int id, Student student, Teacher teacher, LocalDate termin) {
        this.id = id;
        this.student = student;
        this.teacher = teacher;
        this.termin = termin;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public LocalDate getTermin() {
        return termin;
    }

    public void setTermin(LocalDate termin) {
        this.termin = termin;
    }
}
//Lesson (id, Kursant, Nauczyciel, termin)