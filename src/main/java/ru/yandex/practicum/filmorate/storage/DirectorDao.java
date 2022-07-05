package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

public interface DirectorDao {
    Director getDirector(Integer id);

    List<Director> getAll();

    Director addDirector(Director director);

    Director updateDirector(Director director);

    void removeDirector(Integer id);
}
