package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.SearchBy;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class InMemoryFilmService implements FilmService{
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public InMemoryFilmService(@Qualifier("inMemoryFilmStorage") FilmStorage filmStorage,
                               @Qualifier("inMemoryUserStorage") UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public void addLike(int filmId, int userId) {
        if (filmStorage.getFilms().containsKey(filmId)) {
            if (userStorage.getUsers().containsKey(userId)) {
                filmStorage.getFilm(filmId).getLikes().add(userId);
                filmStorage.getFilm(filmId).setRate(filmStorage.getFilm(filmId).getLikes().size());
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
                filmStorage.getFilm(filmId).setRate(filmStorage.getFilm(filmId).getLikes().size());
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

    public List<Film> popularFilms(int count, int genreId, int year) {
        return filmStorage.getAllFilms().stream()
                .sorted(Comparator.comparingInt(Film::getRate).reversed())
                .distinct()
                .limit(count)
                .collect(Collectors.toList());
    }

    @Override
    public Film addFilm(Film film) {
        throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED);
    }

    @Override
    public Film addOrUpdateFilm(Film film) {
        throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED);
    }

    @Override
    public Film getFilm(int id) {
        throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED);
    }

    @Override
    public Collection<Film> getAllFilms() {
        throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED);
    }

    @Override
    public List<Film> filmByDirector(Integer idDirector, String param) {
        throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED);
    }

    @Override
    public List<Genre> getAllGenres() {
        throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED);
    }

    @Override
    public Genre getGenre(int id) {
        throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED);
    }

    @Override
    public List<Mpa> getAllMpa() {
        throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED);
    }

    @Override
    public Mpa getMpa(int id) {
        throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED);
    }

    @Override
    public void deleteFilm(int filmId) {
        throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED);
    }

    @Override
    public List<Film> findCommonFilms(int userId, int friendId) {
        throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED);
    }

    @Override
    public List<Film> searchByTitleOrDirector(String query, List<SearchBy> by) {
        throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED);
    }
}
