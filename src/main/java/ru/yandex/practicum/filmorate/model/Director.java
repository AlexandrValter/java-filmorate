package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class Director {
    private Integer id;

    @NotBlank
    @Size(min = 5)
    private String name;

    public Director(Integer id, String name) {
        this.id = id;
        this.name = name;
    }
}