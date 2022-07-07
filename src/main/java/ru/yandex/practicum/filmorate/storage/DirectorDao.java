package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Set;

public interface DirectorDao {
    Director getDirector(Integer id);

    List<Director> getAll();

    Director addDirector(Director director);

    Director updateDirector(Director director);

    void removeDirector(Integer id);

    Set<Director> getFilmDirector(Integer id);

    void fillingDirectors(Film film);
}
