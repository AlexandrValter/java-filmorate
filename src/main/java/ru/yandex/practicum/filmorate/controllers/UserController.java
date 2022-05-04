package ru.yandex.practicum.filmorate.controllers;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();
    private Integer id;

    @PostMapping
    public void addUser(@RequestBody User user) {
        if (validateUser(user)) {
            setName(user);
            user.setId(makeId());
            users.put(user.getId(), user);
        }
    }

    @GetMapping
    public Map<Integer, User> getUsers() {
        return users;
    }

    @PutMapping
    public void addOrUpdateUser(@RequestBody User user) {
        if (validateUser(user)) {
            if (user.getId() != null) {
                if (users.containsKey(user.getId())) {
                    setName(user);
                    users.put(user.getId(), user);
                }
            } else {
                setName(user);
                user.setId(makeId());
                users.put(user.getId(), user);
            }
        }
    }

    @PatchMapping
    public void updateUser(@RequestBody User user) {
        if (validateUser(user)) {
            if (users.containsKey(user.getId())) {
                setName(user);
                users.put(user.getId(), user);
            }
        }
    }

    private Integer makeId() {
        if (id == null) {
            id = 1;
        } else {
            id++;
        }
        return id;
    }

    private boolean validateUser(User user) {
        if (user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            throw new ValidationException("Веденный e-mail некорректен");
        } else if (user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            throw new ValidationException("Проверьте введеный логин, он не должен содержать пробелы");
        } else if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Введена неверная дата рождения");
        } else {
            return true;
        }
    }

    private void setName(User user) {
        if (user.getName() == null) {
            user.setName(user.getLogin());
        } else if (user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}