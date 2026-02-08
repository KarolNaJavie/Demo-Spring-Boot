package com.example.demo.model;

import lombok.Getter;

@Getter
public enum Language {
    JAVA("Java"),
    C("C"),
    HOLYC("Holy C"),
    LUA("Lua");

    private final String displayName;

    Language(String displayName) {
        this.displayName = displayName;
    }

}
