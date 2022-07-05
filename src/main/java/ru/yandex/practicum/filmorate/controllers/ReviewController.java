package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.review.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;
import ru.yandex.practicum.filmorate.service.ReviewServiceImpl;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping
    public List<Review> getAllReviewByIdFilm(
            @RequestParam(value = "filmId", defaultValue = "-1", required = false) int filmId,
            @RequestParam(value = "count", defaultValue = "10", required = false) int count){
        if (filmId == -1){
            return reviewService.getAllReview();
        }
        return reviewService.getAllReviewByIdFilm(filmId, count);
    }

    @PostMapping
    public Review addNewReview(@Valid @RequestBody Review review){
        return reviewService.addReview(review);
    }

    @PutMapping
    public Review changeReview(@Valid @RequestBody Review review){
        return reviewService.changeReview(review);
    }

    @DeleteMapping("/{idReview}")
    public void deleteReview(@PathVariable("idReview") int idReview){
        reviewService.deleteReview(idReview);
    }

    @GetMapping("/{idReview}")
    public Review findReviewById(@PathVariable("idReview") int idReview){
        return reviewService.findReviewById(idReview);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLikeForReview(@PathVariable("id") int id, @PathVariable("userId") int userId){
        reviewService.addLikeForReview(id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void addDislikeForReview(@PathVariable("id") Integer id, @PathVariable("userId") Integer userId){
        reviewService.addDislikeForReview(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLikeForReview(@PathVariable("id") int id, @PathVariable("userId") int userId){
        reviewService.deleteLikeForReview(id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void deleteDislikeForReview(@PathVariable("id") int id, @PathVariable("userId") int userId){
        reviewService.deleteDislikeForReview(id, userId);
    }
}
