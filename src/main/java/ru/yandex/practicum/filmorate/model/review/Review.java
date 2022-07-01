package ru.yandex.practicum.filmorate.model.review;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Data
public class Review {
    private int id;
    @NotBlank
    private final String content;       //сам текст отзыва
    @NotNull
    private final Boolean isPositive;  //Отзыв создан положительным или отрицательным
    @NotNull
    private final Integer userId;           //чей отзыв
    @NotNull
    private final Integer filmId;           //на какой фильм отзыв
    private Integer useful;                 // количество лайков минус дизлайки. При создании нового равен 0

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Review review = (Review) o;
        return id == review.id && Objects.equals(content, review.content) && Objects.equals(isPositive, review.isPositive) && Objects.equals(userId, review.userId) && Objects.equals(filmId, review.filmId) && Objects.equals(useful, review.useful);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, content, isPositive, userId, filmId, useful);
    }
}
