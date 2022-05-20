package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.time.DurationMin;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.Duration;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class Film {
    private Set<Integer> likes = new HashSet<>();
    @JsonIgnore
    private int countLikes;
    private Integer id;
    @NotBlank(message = "Название не должно быть пустым")
    private final String name;
    @NotBlank(message = "Описание не должно быть пустым")
    @Size(max = 200, message = "Длина описания не должна превышать 200 символов")
    private final String description;
    private final LocalDate releaseDate;
    @DurationMin(message = "Продолжительность не может быть отрицательной")
    private final Duration duration;
}