package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.review.Review;
import ru.yandex.practicum.filmorate.storage.dao.ReviewDaoService;

import java.util.List;

@Slf4j
@Service
public class ReviewService {
    private final ReviewDaoService reviewDaoService;
    private final static Integer LIKE = 1;
    private final static Integer DISLIKE = -1;

    public ReviewService(ReviewDaoService reviewDaoService) {
        this.reviewDaoService =reviewDaoService;
    }

    public List<Review> getAllReviewByIdFilm(Integer filmId, Integer count) {
        return reviewDaoService.getAllReviewByIdFilm(filmId, count);
    }

    public void addLikeForReview(Integer id, Integer userId){
        reviewDaoService.addLikeDislike(id, userId, LIKE);
    }

    public void addDislikeForReview(Integer id, Integer userId){
        reviewDaoService.addLikeDislike(id, userId, DISLIKE);
    }

    public void deleteLikeForReview(Integer id, Integer userId){
        reviewDaoService.deleteLikeDislike(id, userId, DISLIKE);
    }

    public void deleteDislikeForReview(Integer id, Integer userId) {
        reviewDaoService.deleteLikeDislike(id, userId, LIKE);
    }
}
