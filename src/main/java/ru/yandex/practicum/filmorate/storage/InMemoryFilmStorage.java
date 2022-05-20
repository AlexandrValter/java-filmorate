package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Integer, Film> films = new HashMap<>();
    private Integer id;

    @Override
    public Film addFilm(Film film) {
        if (validateFilm(film)) {
            film.setId(makeId());
            films.put(film.getId(), film);
            log.info("Добавлен новый фильм {}, id={}", film.getName(), film.getId());
            return film;
        }
        return null;
    }

    @Override
    public Map<Integer, Film> getFilms() {
        return films;
    }

    @Override
    public Film addOrUpdateFilm(Film film) {
        if (validateFilm(film)) {
            if (film.getId() != null) {
                if (films.containsKey(film.getId())) {
                    films.put(film.getId(), film);
                    log.info("Обновлена информация о фильме {}, id={}", film.getName(), film.getId());
                    return film;
                }
                return null;
            } else {
                film.setId(makeId());
                films.put(film.getId(), film);
                log.info("Добавлен новый фильм {}, id={}", film.getName(), film.getId());
                return film;
            }
        }
        return null;
    }

    @Override
    public Film getFilm(int id) {
        if (films.containsKey(id)) {
            return films.get(id);
        }
        return null;
    }

    private boolean validateFilm(Film film) {
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.warn("Введена неверная дата релиза");
            throw new ValidationException("Неверная дата релиза");
        } else {
            return true;
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
}