package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.review.Review;

import java.util.List;

public interface ReviewStorage {

    List<Review> getAllReview();

    Review addReview(Review review);

    void deleteReview(Integer id);

    Review changeReview(Review review);

    Review findReviewById(Integer id);
}
