package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Data
public class User {
    @JsonIgnore
    private Set<Integer> friends = new HashSet<>();
    private Integer id;
    @NotBlank(message = "Login не должен быть пустым")
    @Pattern(regexp = "\\S+", message = "Login не должен содержать пробелов")
    private final String login;
    @Email(message = "Проверьте корректность ввода e-mail")
    private final String email;
    private String name;
    @PastOrPresent(message = "Дата рождения должна быть не позднее сегодняшнего числа")
    private final LocalDate birthday;

    public User(Integer id, String login, String email, String name, LocalDate birthday) {
        this.id = id;
        this.login = login;
        this.email = email;
        this.name = name;
        this.birthday = birthday;
    }

    public String getName() {
        if (this.name == null) {
            log.warn("Имя не введено");
            this.name = this.login;
            return this.name;
        } else if (this.name.isBlank()) {
            log.warn("Введено пустое имя");
            this.name = this.login;
            return this.name;
        }
        return this.name;
    }
}