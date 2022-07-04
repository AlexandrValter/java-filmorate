package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.review.Review;

import java.util.List;

public interface ReviewService {

    List<Review> getAllReviewByIdFilm(Integer filmId, Integer count);

    void addLikeForReview(Integer id, Integer userId);

    void addDislikeForReview(Integer id, Integer userId);

    void deleteLikeForReview(Integer id, Integer userId);

    void deleteDislikeForReview(Integer id, Integer userId);

    List<Review> getAllReview();

    Review addReview(Review review);

    Review changeReview(Review review);

    void deleteReview(int idReview);

    Review findReviewById(int idReview);
}