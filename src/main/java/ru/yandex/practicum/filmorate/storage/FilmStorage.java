package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface FilmStorage {

    Film addFilm(Film film);

    Map<Integer, Film> getFilms();

    Film addOrUpdateFilm(Film film);

    Film getFilm(int id);

    Collection<Film> getAllFilms();

    List<Film> getPopularFilms(int count);

    void deleteFilm(int filmId);

    Set<Film> getUserLikedFilms(int userId);

    List<Film> getPopularFilmsByYear(int count, int year);

    List<Film> getPopularFilmsByGenre(int count, int genreId);

    List<Film> getFilmsByDirector(Integer idDirector, String param);

    List<Film> getPopularFilmsByGenreAndYear(int count, int genreId, int year);
}