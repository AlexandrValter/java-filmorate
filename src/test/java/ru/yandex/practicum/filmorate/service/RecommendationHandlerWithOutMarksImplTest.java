package ru.yandex.practicum.filmorate.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Set;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class RecommendationHandlerWithOutMarksImplTest {

    private final RecommendationHandler rh;
    private final UserService userService;
    private final FilmService filmService;
@Autowired
    public RecommendationHandlerWithOutMarksImplTest(RecommendationHandler rh,
                                                     @Qualifier("UserDbService") UserService userService,
                                                     @Qualifier("FilmDbService") FilmService filmService) {
        this.rh = rh;
        this.userService = userService;
        this.filmService = filmService;
    }

    @Test
    public void fimdRecommendation() {
        initData();
        Set<Film> films = rh.findRecommendation(1);
        assertEquals(2,rh.findRecommendation(1).size());
        assertTrue(rh.findRecommendation(1).contains(filmService.getFilm(8)) &&
                rh.findRecommendation(1).contains(filmService.getFilm(5)));

    }

    private void initData() {
        for (int i = 1; i <= 10; i++) {
            User user = new User(null, ("user-" + i), "user@jjj.ff", "", LocalDate.now());
            userService.addUser(user);
            Film film = new Film(null, ("Film-" + i), "", LocalDate.now(), 100);
            film.setMpa(filmService.getMpa(1));
            filmService.addFilm(film);
        }
        filmService.addLike(1, 1);
        filmService.addLike(2, 1);
        filmService.addLike(3, 1);
        filmService.addLike(1, 2);
        filmService.addLike(2, 2);
        filmService.addLike(3, 2);
        filmService.addLike(8, 2);
        filmService.addLike(1, 3);
        filmService.addLike(2, 3);
        filmService.addLike(3, 3);
        filmService.addLike(5, 3);
        filmService.addLike(1, 4);
        filmService.addLike(2, 4);
        filmService.addLike(7, 4);
        filmService.addLike(5, 4);
        filmService.addLike(6, 5);
        filmService.addLike(5, 5);
        filmService.addLike(8, 5);
        filmService.addLike(9, 5);


    }
}