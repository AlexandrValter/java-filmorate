package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class User {
    private Set<Integer> friends = new HashSet<>();
    private Integer id;
    private final String email;
    private final String login;
    private String name;
    private final LocalDate birthday;
}
