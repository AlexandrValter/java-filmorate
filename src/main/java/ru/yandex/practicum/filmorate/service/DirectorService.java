package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

public interface DirectorService {
    Director getDirector(Integer id);

    List<Director> getAll();

    Director addDirector(Director director);

    Director updateDirector(Director director);

    void removeDirector(Integer id);
}
