package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class Like {
    private final int filmId;
    private final int userId;

    public Like(int userId, int filmId) {
        this.userId= userId;
        this.filmId= filmId;
    }
}