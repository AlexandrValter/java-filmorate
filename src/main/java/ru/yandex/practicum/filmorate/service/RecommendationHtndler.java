package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Set;

public interface RecommendationHtndler {
    Set<Film> findRecommendation(int userId);
}
