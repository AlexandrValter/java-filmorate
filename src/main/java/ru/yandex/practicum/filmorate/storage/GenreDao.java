package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.TreeSet;

public interface GenreDao {

    List<Genre> getAllGenres();

    Genre getGenre(int id);

    TreeSet<Genre> getFilmGenres(int id);

    void fillingGenres(Film film);
}
