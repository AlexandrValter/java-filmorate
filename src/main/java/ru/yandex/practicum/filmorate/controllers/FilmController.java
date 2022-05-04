package ru.yandex.practicum.filmorate.controllers;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
public class FilmController {
    private final Map<Integer, Film> films = new HashMap<>();
    private Integer id;

    @PostMapping
    public void addFilm(@RequestBody Film film) {
        if (validateFilm(film)) {
            film.setId(makeId());
            films.put(film.getId(), film);
        }
    }

    @GetMapping
    public Map<Integer, Film> getFilms() {
        return films;
    }

    @PutMapping
    public void addOrUpdateFilm(@RequestBody Film film) {
        if (validateFilm(film)) {
            if (film.getId() != null) {
                if (films.containsKey(film.getId())) {
                    films.put(film.getId(), film);
                }
            } else {
                film.setId(makeId());
                films.put(film.getId(), film);
            }
        }
    }

    @PatchMapping
    public void updateFilm(@RequestBody Film film) {
        if (validateFilm(film)) {
            if (films.containsKey(film.getId())) {
                films.put(film.getId(), film);
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

    private boolean validateFilm(Film film) {
        if (film.getName().isBlank()) {
            throw new ValidationException("Название фильма не может быть пустым");
        } else if (film.getDescription().length() > 200) {
            throw new ValidationException("Максимальная длина описания - 200 символов");
        } else if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Неверная дата релиза");
        } else if (film.getDuration().isNegative()) {
            throw new ValidationException("Продолжительность фильма должна быть положительной");
        } else {
            return true;
        }
    }
}