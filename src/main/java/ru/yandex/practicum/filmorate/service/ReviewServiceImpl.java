package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationReviewException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.review.Review;
import ru.yandex.practicum.filmorate.storage.*;

import java.util.List;

@Slf4j
@Service
public class ReviewServiceImpl implements ReviewService {
    private final ReviewStorage reviewStorage;
    private final ReviewDbService reviewDbService;
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private final FeedDao feedDao;
    private final static Integer LIKE = 1;
    private final static Integer DISLIKE = -1;

    public ReviewServiceImpl(ReviewStorage reviewStorage,
                             ReviewDbService reviewDbService,
                             @Qualifier("UserDbStorage") UserStorage userStorage,
                             @Qualifier("FilmDbStorage") FilmStorage filmStorage,
                             FeedDao feedDao) {
        this.reviewStorage = reviewStorage;
        this.reviewDbService = reviewDbService;
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
        this.feedDao = feedDao;
    }

    @Override
    public List<Review> getAllReviewByIdFilm(Integer filmId, Integer count) {
        log.info("Запрошены {} отзывов на фильм id = {}", count, filmId);
        return reviewDbService.getAllReviewByIdFilm(filmId, count);
    }

    @Override
    public void addLikeForReview(Integer id, Integer userId) {
        log.info("Пользователю id = {} понравился отзыв id = {}", userId, id);
        reviewDbService.addLikeDislike(id, userId, LIKE);
    }

    @Override
    public void addDislikeForReview(Integer id, Integer userId) {
        log.info("Пользователю id = {} не понравился отзыв id = {}", userId, id);
        reviewDbService.addLikeDislike(id, userId, DISLIKE);
    }

    @Override
    public void deleteLikeForReview(Integer id, Integer userId) {
        log.info("Пользователь id = {} удалил лайк с отзыва id = {}", userId, id);
        reviewDbService.deleteLikeDislike(id, userId, DISLIKE);
    }

    @Override
    public void deleteDislikeForReview(Integer id, Integer userId) {
        log.info("Пользователь id = {} удалил дизлайк с отзыва id = {}", userId, id);
        reviewDbService.deleteLikeDislike(id, userId, LIKE);
    }

    @Override
    public List<Review> getAllReview() {
        log.info("Запрошены все отзывы");
        return reviewStorage.getAllReview();
    }

    @Override
    public Review addReview(Review review) {
        if (filmStorage.getFilm(review.getFilmId()) instanceof Film &&
                userStorage.getUser(review.getUserId()) instanceof User) {
            Review newReview = reviewStorage.addReview(review);
            log.info("Добавлен отзыв id = {}", review.getId());
            feedDao.addFeed(newReview.getUserId(), Event.REVIEW, Operation.ADD, newReview.getId());
            return newReview;
        } else {
            throw new ValidationReviewException("Некоректные данные, отзыв не добавлен");
        }
    }

    @Override
    public Review changeReview(Review review) {
        if (reviewStorage.findReviewById(review.getId())!=null) {
            log.info("Изменен отзыв id = {}", review.getId());
            Review newReview = reviewStorage.changeReview(review);
            feedDao.addFeed(newReview.getUserId(), Event.REVIEW, Operation.UPDATE, newReview.getId());
            return newReview;
        } else {
            throw new ValidationReviewException("Некоректные данные, отзыв не изменен");
        }
    }

    @Override
    public void deleteReview(int idReview) {
        feedDao.addFeed(reviewStorage.findReviewById(idReview).getUserId(), Event.REVIEW, Operation.REMOVE, idReview);
        log.info("Удален отзыв id = {}", idReview);
        reviewStorage.deleteReview(idReview);
    }

    @Override
    public Review findReviewById(int idReview) {
        log.info("Запрошен отзыв id = {}", idReview);
        return reviewStorage.findReviewById(idReview);
    }
}