package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.review.Review;

import java.util.List;

public interface ReviewDbService {
    List<Review> getAllReviewByIdFilm(Integer filmId, Integer count);

    void addLikeDislike(Integer id, Integer userId, Integer value);

    void deleteLikeDislike(Integer id, Integer userId, Integer value);
}
