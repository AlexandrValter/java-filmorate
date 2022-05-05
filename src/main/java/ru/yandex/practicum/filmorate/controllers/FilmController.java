package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final Map<Integer, Film> films = new HashMap<>();
    private Integer id;

    @PostMapping
    public Film addFilm(@RequestBody Film film) {
        if (validateFilm(film)) {
            film.setId(makeId());
            films.put(film.getId(), film);
            log.info("Добавлен новый фильм {}, id={}", film.getName(), film.getId());
            return film;
        }
        return null;
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
                    log.info("Обновлена информация о фильме {}, id={}", film.getName(), film.getId());
                }
            } else {
                film.setId(makeId());
                films.put(film.getId(), film);
                log.info("Добавлен новый фильм {}, id={}", film.getName(), film.getId());
            }
        }
    }

    @PatchMapping
    public void updateFilm(@RequestBody Film film) {
        if (validateFilm(film)) {
            if (films.containsKey(film.getId())) {
                films.put(film.getId(), film);
                log.info("Обновлена информация о фильме {}, id={}", film.getName(), film.getId());
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
            log.error("Название фильма не введено");
            throw new ValidationException("Название фильма не может быть пустым");
        } else if (film.getDescription().length() > 200) {
            log.error("Превышена максимальная длина описания");
            throw new ValidationException("Максимальная длина описания - 200 символов");
        } else if (film.getDescription().isBlank()) {
            log.error("Не введено описание фильма");
            throw new ValidationException("Необходимо ввести описание фильма");
        } else if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.error("Введена неверная дата релиза");
            throw new ValidationException("Неверная дата релиза");
        } else if (film.getDuration().isNegative()) {
            log.error("Введена отрицательная продолжительность фильма");
            throw new ValidationException("Продолжительность фильма должна быть положительной");
        } else {
            return true;
        }
    }
}