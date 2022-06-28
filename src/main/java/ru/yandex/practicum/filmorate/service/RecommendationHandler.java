package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Set;

public interface RecommendationHandler {
    Set<Film> findRecommendation(int userId);
}
