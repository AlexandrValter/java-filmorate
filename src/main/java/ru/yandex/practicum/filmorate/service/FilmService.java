package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(InMemoryFilmStorage filmStorage, InMemoryUserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public void addLike(int filmId, int userId) {
        if (filmStorage.getFilms().containsKey(filmId)) {
            if (userStorage.getUsers().containsKey(userId)) {
                filmStorage.getFilm(filmId).getLikes().add(userId);
                filmStorage.getFilm(filmId).setCountLikes(filmStorage.getFilm(filmId).getLikes().size());
                log.info("Пользователь {} поставил лайк фильму {}", userStorage.getUser(userId).getEmail(), filmStorage.getFilm(filmId).getName());
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                        String.format("Пользователь с id %d не найден", userId));
            }
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    String.format("Фильм с id %d не найден", filmId));
        }
    }

    public void deleteLike(int filmId, int userId) {
        if (filmStorage.getFilms().containsKey(filmId)) {
            if (userStorage.getUsers().containsKey(userId)) {
                filmStorage.getFilm(filmId).getLikes().remove(userId);
                filmStorage.getFilm(filmId).setCountLikes(filmStorage.getFilm(filmId).getLikes().size());
                log.info("Пользователь {} удалил лайк с фильма {}", userStorage.getUser(userId).getEmail(), filmStorage.getFilm(filmId).getName());
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                        String.format("Пользователь с id %d не найден", userId));
            }
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    String.format("Фильм с id %d не найден", filmId));
        }
    }

    public List<Film> popularFilms(int count) {
        return filmStorage.getAllFilms().stream()
                .sorted(Comparator.comparingInt(Film::getCountLikes).reversed())
                .distinct()
                .limit(count)
                .collect(Collectors.toList());
    }
}
