package ru.yandex.practicum.filmorate.storage.dao;

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

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ReviewDbStorageTest {

    private final ReviewDbStorage reviewDbStorage;
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

    Review reviewOne = new Review("Text review",true,1,1);
    Review reviewTwo = new Review("Text review Two",false,1,1);
    Review reviewNew = new Review("Text review New",false,1,1);

    private void addAllDataForTest(){
        userDbStorage.addUser(userOne);
        userDbStorage.addUser(userTwo);
        filmOne.setMpa(mpa);
        filmTwo.setMpa(mpa);
        filmDbStorage.addFilm(filmOne);
        filmDbStorage.addFilm(filmTwo);
    }

    @Test
    void getAllReview() {
        assertTrue(reviewDbStorage.getAllReview().isEmpty());
    }

    @Test
    void addReview() {
        assertTrue(reviewDbStorage.getAllReview().isEmpty());
        addAllDataForTest();
        reviewDbStorage.addReview(reviewOne);
        assertFalse(reviewDbStorage.getAllReview().isEmpty());
    }

    @Test
    void deleteReview() {
        addAllDataForTest();
        reviewDbStorage.addReview(reviewOne);
        reviewDbStorage.addReview(reviewTwo);
        assertTrue(reviewDbStorage.findReviewById(2).getContent()
                .equals(reviewTwo.getContent()));
        reviewDbStorage.deleteReview(2);
        assertThrows(NotFoundReviewException.class,() -> reviewDbStorage.findReviewById(2));
    }

    @Test
    void changeReview() {
        addAllDataForTest();
        reviewDbStorage.addReview(reviewOne);
        reviewDbStorage.addReview(reviewTwo);
        assertTrue(reviewDbStorage.findReviewById(1).getContent()
                .equals(reviewOne.getContent()));
        reviewNew.setId(1);
        reviewDbStorage.changeReview(reviewNew);
        assertTrue(reviewDbStorage.findReviewById(1).getContent()
                .equals(reviewNew.getContent()));
    }

    @Test
    void findReviewById() {
        addAllDataForTest();
        reviewDbStorage.addReview(reviewOne);
        reviewDbStorage.addReview(reviewTwo);
        assertTrue(reviewDbStorage.findReviewById(1).getContent()
                .equals(reviewOne.getContent()));
    }
}