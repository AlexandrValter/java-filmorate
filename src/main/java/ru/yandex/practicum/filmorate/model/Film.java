package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

@Data
public class Film {
    @JsonIgnore
    private Set<Integer> likes = new HashSet<>();
    @JsonIgnore
    private int rate;
    private Integer id;
    @NotBlank(message = "Название не должно быть пустым")
    private final String name;
    @NotBlank(message = "Описание не должно быть пустым")
    @Size(max = 200, message = "Длина описания не должна превышать 200 символов")
    private final String description;
    private final LocalDate releaseDate;
    @Min(0)
    private final Integer duration;
    private TreeSet<Genre> genres;
    private Mpa mpa;
    private Set<Director> directors;

    public Film(Integer id,
                String name,
                String description,
                LocalDate releaseDate,
                Integer duration) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }
}