package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public Collection<Film> getAllFilms() {
        throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED);
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED);
    }

    @Override
    public void deleteFilm(int filmId) {
        throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED);
    }

    @Override
    public List<Film> getPopularFilmsByYear(int count, int year) {
        throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED);
    }

    @Override
    public List<Film> getPopularFilmsByGenre(int count, int genreId) {
        throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED);
    }

    @Override
    public List<Film> getPopularFilmsByGenreAndYear(int count, int genreId, int year) {
        throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED);
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
                } else {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                            String.format("Фильм с id %d не найден", film.getId()));
                }
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
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    String.format("Фильм с id %d не найден", id));
        }
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