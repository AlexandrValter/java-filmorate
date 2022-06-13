package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class User {
    @JsonIgnore
    private Set<Integer> friends = new HashSet<>();
    private Integer id;
    @Email(message = "Проверьте корректность ввода e-mail")
    private final String email;
    @NotBlank(message = "Login не должен быть пустым")
    @Pattern(regexp = "\\S+", message = "Login не должен содержать пробелов")
    private final String login;
    private String name;
    @PastOrPresent(message = "Дата рождения должна быть не позднее сегодняшнего числа")
    private final LocalDate birthday;

    public User(Integer id, String email, String login, String name, LocalDate birthday) {
        this.id = id;
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
    }
}