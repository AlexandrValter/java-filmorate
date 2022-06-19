package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.NonNull;

@Data
public class Mpa {
    @NonNull
    private final int id;
    private String name;

    public Mpa(@NonNull int id, String name) {
        this.id = id;
        this.name = name;
    }
}