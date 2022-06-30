package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface FilmService {

    void addLike(int filmId, int userId);

    void deleteLike(int filmId, int userId);

    List<Film> popularFilms(int count);

    Film addFilm(Film film);

    Film addOrUpdateFilm(Film film);

    Film getFilm(int id);

    Collection<Film> getAllFilms();

    List<Genre> getAllGenres();

    Genre getGenre(int id);

    List<Mpa> getAllMpa();

    Mpa getMpa(int id);

    void deleteFilm(int filmId);

    Set<Film> findCommonFilms(int userId, int friendId);
}
