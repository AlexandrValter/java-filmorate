package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class recommendationHandlerWithOutMarksImplTest {

    private final RecommendationHandler rh;
    private final UserService userService;
    private final FilmService filmService;

    @Test
    @DirtiesContext
    public void fimdRecommendation() {

        rh.findRecommendation(1);
    }
    private void initData(){
        for (int i = 1; i < 15; i++) {
            User user = new User(null,("user-"+i),"user@jjj.ff","", LocalDate.now());
            userService.addUser(user);
            Film film = new Film(null,("Film-"+i),"",LocalDate.now(), 100);
            filmService.addFilm(film);
        }

    }
}