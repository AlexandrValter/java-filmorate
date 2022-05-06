package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;

@Data
public class User {
    private Integer id;
    @Email(message = "Проверьте корректность ввода e-mail")
    private final String email;
    @NotBlank(message = "Login не должен быть пустым")
    @Pattern(regexp = "\\S+", message = "Login не должен содержать пробелов")
    private final String login;
    private String name;
    @PastOrPresent(message = "Дата рождения должна быть не позднее сегодняшнего числа")
    private final LocalDate birthday;
}