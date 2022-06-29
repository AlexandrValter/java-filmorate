package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;
import java.util.List;

public interface FilmService {

    void addLike(int filmId, int userId);

    void deleteLike(int filmId, int userId);

    List<Film> popularFilms(int count);

    List<Film> filmByDirector(Integer idDirector,String param);

    Film addFilm(Film film);

    Film addOrUpdateFilm(Film film);

    Film getFilm(int id);

    Collection<Film> getAllFilms();

    List<Genre> getAllGenres();

    Genre getGenre(int id);

    List<Mpa> getAllMpa();

    Mpa getMpa(int id);

    void deleteFilm(int filmId);
}
