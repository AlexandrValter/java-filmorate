package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewStorage {

    List<Review> getAllReview();

    Review addReview(Review review);

    void deleteReview(Integer id);

    Review changeReview(Review review);

    Review findReviewById(Integer id);

    List<Review> getAllReviewByIdFilm(Integer filmId, Integer count);

    void addLikeDislike(Integer id, Integer userId, Integer value);

    void deleteLikeDislike(Integer id, Integer userId, Integer value);
}