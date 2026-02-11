package com.example.demo.model;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor //@NoArgsConstructor
@NoArgsConstructor
@Builder
public class Teacher {
    int id;
    String name;
    String surname;
    List<Language> language;

    @Override
    public String toString() {
        return name + " " + surname;
    }
    public void addLanguage(Language language){
        this.language.add(language);
    }
//    public Teacher(int id, String name, String surname, List<String> language) {
//        this.id = id;
//        this.name = name;
//        this.surname = surname;
//        this.language = language;
//    }
//    Lombok tworzy je za nas

//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public String getSurname() {
//        return surname;
//    }

//    public void setSurname(String surname) {
//        this.surname = surname;
//    }

//    public int getId() {
//        return id;
//    }

//    public void setId(int id) {
//        this.id = id;
//    }

//    public List<String> getLanguage() {
//        return language;
//    }

//    public void setLanguage(List<String> language) {
//        this.language = language;
//    }
}
