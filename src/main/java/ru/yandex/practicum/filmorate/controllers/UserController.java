package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();
    private Integer id;

    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
        setName(user);
        user.setId(makeId());
        users.put(user.getId(), user);
        log.info("Добавлен новый пользователь {}, id={}", user.getName(), user.getId());
        return user;
    }

    @GetMapping
    public Map<Integer, User> getUsers() {
        return users;
    }

    @PutMapping
    public void addOrUpdateUser(@Valid @RequestBody User user) {
        if (user.getId() != null) {
            if (users.containsKey(user.getId())) {
                setName(user);
                users.put(user.getId(), user);
                log.info("Обновлена информация о пользователе {}, id={}", user.getName(), user.getId());
            }
        } else {
            setName(user);
            user.setId(makeId());
            users.put(user.getId(), user);
            log.info("Добавлен новый пользователь {}, id={}", user.getName(), user.getId());
        }
    }

    @PatchMapping
    public void updateUser(@Valid @RequestBody User user) {
        if (users.containsKey(user.getId())) {
            setName(user);
            users.put(user.getId(), user);
            log.info("Обновлена информация о пользователе {}, id={}", user.getName(), user.getId());
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

    private void setName(User user) {
        if (user.getName() == null) {
            log.warn("Имя не введено");
            user.setName(user.getLogin());
        } else if (user.getName().isBlank()) {
            log.warn("Введено пустое имя");
            user.setName(user.getLogin());
        }
    }
}