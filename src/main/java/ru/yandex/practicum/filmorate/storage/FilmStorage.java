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
}