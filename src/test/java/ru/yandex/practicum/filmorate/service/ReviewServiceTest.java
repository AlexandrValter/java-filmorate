package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exception.NotFoundReviewException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.review.Review;
import ru.yandex.practicum.filmorate.storage.dao.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.dao.ReviewDbStorage;
import ru.yandex.practicum.filmorate.storage.dao.UserDbStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ReviewServiceTest {

    private final ReviewDbStorage reviewDbStorage;
    private final ReviewService reviewService;
    private final FilmDbStorage filmDbStorage;
    private final UserDbStorage userDbStorage;

    User userOne = new User(1,
            "login",
            "email@email.ru",
            "nameUserOne",
            LocalDate.of(1987,05,05)
    );

    User userTwo = new User(2,
            "login_2",
            "email_2@email.ru",
            "nameUserTwo",
            LocalDate.of(1987,05,05)
    );

    Mpa mpa = new Mpa(1,"Комедия");

    Film filmOne = new Film(1,
            "Name Film",
            "description",
            LocalDate.of(1990, 05,05),120
    );

    Film filmTwo = new Film(2,"Name Film_2","description_2",
            LocalDate.of(1990, 05,05),120
    );

    Review reviewFilmOne = new Review("Text review",true,1,1);
    Review reviewFilmOneNext = new Review("Text review Two",false,1,1);
    Review reviewFilmTwo = new Review("Text review New",false,1,2);


    private void addAllDataForTest(){
        userDbStorage.addUser(userOne);
        userDbStorage.addUser(userTwo);
        filmOne.setMpa(mpa);
        filmTwo.setMpa(mpa);
        filmDbStorage.addFilm(filmOne);
        filmDbStorage.addFilm(filmTwo);
        reviewDbStorage.addReview(reviewFilmOne);
        reviewDbStorage.addReview(reviewFilmOneNext);
        reviewDbStorage.addReview(reviewFilmTwo);
    }


    @Test
    void getAllReviewByIdFilm() {
        assertTrue(reviewService.getAllReviewByIdFilm(1, 3).isEmpty());
        addAllDataForTest();
        assertTrue(reviewService.getAllReviewByIdFilm(1,3).size() == 2);
    }

    @Test
    void addLikeForReview() {
        assertThrows(NotFoundReviewException.class, () -> reviewService.addLikeForReview(1, 1));
        addAllDataForTest();
        assertTrue(reviewDbStorage.findReviewById(1).getUseful() == 0);
        reviewService.addLikeForReview(1, 1);
        assertTrue(reviewDbStorage.findReviewById(1).getUseful() == 1);
        reviewService.addLikeForReview(1, 2);
        assertTrue(reviewDbStorage.findReviewById(1).getUseful() == 2);
    }

    @Test
    void addDislikeForReview() {
        assertThrows(NotFoundReviewException.class, () -> reviewService.addLikeForReview(1, 1));
        addAllDataForTest();
        assertTrue(reviewDbStorage.findReviewById(1).getUseful() == 0);
        reviewService.addDislikeForReview(1, 1);
        assertTrue(reviewDbStorage.findReviewById(1).getUseful() == -1);
        reviewService.addDislikeForReview(1, 2);
        assertTrue(reviewDbStorage.findReviewById(1).getUseful() == -2);
    }

    @Test
    void deleteLikeForReview() {
        assertThrows(NotFoundReviewException.class, () -> reviewService.addLikeForReview(1, 1));
        addAllDataForTest();
        reviewService.addLikeForReview(1, 1);
        reviewService.addLikeForReview(1, 2);
        assertTrue(reviewDbStorage.findReviewById(1).getUseful() == 2);
        reviewService.deleteLikeForReview(1,2);
        assertTrue(reviewDbStorage.findReviewById(1).getUseful() == 1);
    }

    @Test
    void deleteDislikeForReview() {
        assertThrows(NotFoundReviewException.class, () -> reviewService.addLikeForReview(1, 1));
        addAllDataForTest();
        reviewService.addDislikeForReview(1, 1);
        reviewService.addDislikeForReview(1, 2);
        assertTrue(reviewDbStorage.findReviewById(1).getUseful() == -2);
        reviewService.deleteDislikeForReview(1,2);
        assertTrue(reviewDbStorage.findReviewById(1).getUseful() == -1);
    }
}