package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final List<Film> popularFilms = new ArrayList<>();

    @Autowired
    public FilmService(InMemoryFilmStorage filmStorage, InMemoryUserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public void addLike(int filmId, int userId) {
        if (filmStorage.getFilms().containsKey(filmId)) {
            if (userStorage.getUsers().containsKey(userId)) {
                filmStorage.getFilm(filmId).getLikes().add(userId);
                popularFilms.add(filmStorage.getFilm(filmId));
                filmStorage.getFilm(filmId).setCountLikes(filmStorage.getFilm(filmId).getLikes().size());
                log.info("Пользователь {} поставил лайк фильму {}", userStorage.getUser(userId).getEmail(), filmStorage.getFilm(filmId).getName());
            }
        }
    }

    public void deleteLike(int filmId, int userId) {
        if (filmStorage.getFilms().containsKey(filmId)) {
            if (userStorage.getUsers().containsKey(userId)) {
                filmStorage.getFilm(filmId).getLikes().remove(userId);
                filmStorage.getFilm(filmId).setCountLikes(filmStorage.getFilm(filmId).getLikes().size());
                log.info("Пользователь {} удалил лайк с фильма {}", userStorage.getUser(userId).getEmail(), filmStorage.getFilm(filmId).getName());
            }
        }
    }

    public List<Film> getPopularFilms(int count) {
        return popularFilms.stream()
                .sorted((Comparator.comparing(Film::getCountLikes).reversed()))
                .distinct()
                .limit(count)
                .collect(Collectors.toList());
    }
}
