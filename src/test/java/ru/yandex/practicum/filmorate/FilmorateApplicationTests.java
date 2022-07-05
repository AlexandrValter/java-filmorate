package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.review.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;
import ru.yandex.practicum.filmorate.service.dao.FilmDbService;
import ru.yandex.practicum.filmorate.service.dao.UserDbService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.LikeDao;

import java.time.LocalDate;
import java.util.List;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class FilmorateApplicationTests {

    private final UserDbService userService;
    private final FilmDbService filmService;
    private final FilmStorage filmStorage;
    private final ReviewService reviewService;
    private final LikeDao likeDao;

    private User user1 = new User(
            1,
            "test_login1",
            "test1@email.ru",
            "test name1",
            LocalDate.of(1999, 7, 15)
    );
    private User user2 = new User(
            2,
            "test_login2",
            "test2@email.ru",
            "test name2",
            LocalDate.of(1998, 6, 14)
    );
    private User user3 = new User(
            3,
            "test_login3",
            "test3@email.ru",
            "test name3",
            LocalDate.of(1997, 5, 13)
    );
    private Film film1 = new Film(
            1,
            "film1",
            "nature film about Russian forest",
            LocalDate.of(1997, 5, 13),
            60);
    private Film film2 = new Film(
            2,
            "film2",
            "documentary film about english football",
            LocalDate.of(2007, 7, 21),
            80);

    @Test
    public void test1_addUser() {
        assertTrue(userService.getAllUsers().isEmpty());
        userService.addUser(user1);
        userService.addUser(user2);
        assertEquals(2, userService.getAllUsers().size());
        userService.addUser(user3);
        assertEquals(3, userService.getAllUsers().size());
    }

    @Test
    public void test2_addOrUpdateUser() {
        assertTrue(userService.getAllUsers().isEmpty());
        userService.addUser(user1);
        assertEquals(1, userService.getAllUsers().size());
        User user = userService.getUser(1);
        user.setName("change name for test");
        userService.addOrUpdateUser(user);
        assertEquals("change name for test", userService.getUser(1).getName());
    }

    @Test
    public void test3_addFilm() {
        assertTrue(filmService.getAllFilms().isEmpty());
        film1.setMpa(new Mpa(1, "G"));
        filmService.addFilm(film1);
        assertEquals(1, filmService.getAllFilms().size());
        film2.setMpa(new Mpa(2, "PG"));
        filmService.addFilm(film2);
        assertEquals(2, filmService.getAllFilms().size());
    }

    @Test
    public void test4_addOrUpdateFilm() {
        assertTrue(filmService.getAllFilms().isEmpty());
        film1.setMpa(new Mpa(1, "G"));
        filmService.addFilm(film1);
        assertEquals(1, filmService.getAllFilms().size());
        assertEquals("nature film about Russian forest", filmService.getFilm(1).getDescription());
        film2.setId(1);
        film2.setMpa(new Mpa(2, "PG"));
        filmService.addOrUpdateFilm(film2);
        assertEquals(1, filmService.getAllFilms().size());
        assertEquals("documentary film about english football", filmService.getFilm(1).getDescription());
    }

    @Test
    public void test5_checkMpaDao() {
        assertEquals(5, filmService.getAllMpa().size());
        assertEquals("NC-17", filmService.getMpa(5).getName());
        assertEquals("PG-13", filmService.getMpa(3).getName());
    }

    @Test
    public void test6_checkGenreDao() {
        assertEquals(6, filmService.getAllGenres().size());
        assertEquals("Драма", filmService.getGenre(2).getName());
        assertEquals("Триллер", filmService.getGenre(4).getName());
        assertEquals("Боевик", filmService.getGenre(6).getName());
    }

    @Test
    public void test7_checkMethodsForLikes() {
        film1.setMpa(new Mpa(1, "G"));
        filmService.addFilm(film1);
        film2.setMpa(new Mpa(2, "PG"));
        filmService.addFilm(film2);
        userService.addUser(user1);
        userService.addUser(user2);
        userService.addUser(user3);
        filmService.addLike(1, 2);
        filmService.addLike(1, 1);
        filmService.addLike(2, 3);
        List<Film> films = List.of(film1, film2);
        assertEquals(films, filmService.popularFilms(3, -1, -1));
        filmService.deleteLike(1, 1);
        filmService.addLike(2, 1);
        assertNotEquals(films, filmService.popularFilms(3, -1, -1));
        List<Film> films2 = List.of(film2, film1);
        assertEquals(films2, filmService.popularFilms(3, -1, -1));
    }

    @Test
    public void test8_checkMethodsForFriends() {
        userService.addUser(user1);
        userService.addUser(user2);
        userService.addUser(user3);
        userService.addFriends(1, 2);
        userService.addFriends(1, 3);
        assertEquals(2, userService.getFriends(1).size());
        assertTrue(userService.getCommonFriends(1, 2).isEmpty());
        userService.addFriends(2, 3);
        List<User> commonFriends = List.of(user3);
        assertEquals(commonFriends, userService.getCommonFriends(1, 2));
        userService.deleteFriends(1, 2);
        userService.deleteFriends(1, 3);
        assertTrue(userService.getFriends(1).isEmpty());
    }

    @Test
    public void test9_checkDeleteUser() {
        userService.addUser(user1);
        userService.addUser(user2);
        userService.addFriends(user1.getId(), user2.getId());
        userService.addFriends(user2.getId(), user1.getId());
        film1.setMpa(new Mpa(2, "PG"));
        filmService.addFilm(film1);
        filmService.addLike(film1.getId(), user1.getId());

        userService.deleteUser(user1.getId());

        assertEquals(1, userService.getAllUsers().size());
        assertFalse(userService.getUser(user2.getId()).getFriends().contains(user1.getId()));
        assertEquals(0, filmService.getFilm(film1.getId()).getLikes().size());
    }

    @Test
    public void test10_checkDeleteFilm() {
        film1.setMpa(filmService.getMpa(1));
        userService.addUser(user1);
        filmService.addFilm(film1);
        filmService.addLike(film1.getId(), user1.getId());
        film1.setGenres(new TreeSet<>(filmService.getAllGenres()));

        filmService.deleteFilm(film1.getId());

        assertEquals(0, filmService.getAllFilms().size());
        assertEquals(0, likeDao.getAllLikes().size());
    }

    @Test
    public void test11_checkPopularFilms() {
        film1.setMpa(new Mpa(1, "G"));
        Genre genre1 = new Genre(1, "Комедия");
        film1.setGenres(new TreeSet<>(List.of(genre1)));
        filmService.addFilm(film1);
        film2.setMpa(new Mpa(2, "PG"));
        Genre genre2 = new Genre(2, "Драма");
        film2.setGenres(new TreeSet<>(List.of(genre2)));
        filmService.addFilm(film2);
        Film film3 = new Film(
                3,
                "Test film 3",
                "Film for test 3",
                LocalDate.of(2017, 7, 21),
                100);
        Genre genre3 = new Genre(3, "Мультфильм");
        film3.setGenres(new TreeSet<>(List.of(genre3)));
        film3.setMpa(new Mpa(3, "PG-13"));
        filmService.addFilm(film3);
        assertEquals(List.of(film1), filmService.popularFilms(3, -1, 1997));
        assertEquals(List.of(film2), filmService.popularFilms(3, 2, -1));
        assertEquals(List.of(film3), filmService.popularFilms(3, 3, 2017));
    }

    @Test
    public void test10_checkFeeds() {
        userService.addUser(user1);
        userService.addUser(user2);
        userService.addUser(user3);
        userService.addFriends(1, 2);
        userService.addFriends(1, 3);
        userService.deleteFriends(1, 3);
        assertEquals(3, userService.getFeeds(1).size());
        film1.setMpa(new Mpa(1, "G"));
        filmService.addFilm(film1);
        filmService.addLike(1, 1);
        reviewService.addReview(new Review("Test", true, 1, 1));
        assertEquals(5, userService.getFeeds(1).size());
    }
    @Test
    public void test9_checkCommonFilms() {
        Mpa mpa = filmService.getMpa(1);
        for (int i = 1; i < 5; i++) {
            Film film = new Film(i, ("Film" + 1), "", LocalDate.now(), 50);
            film.setMpa(mpa);
            filmService.addFilm(film);
        }
        userService.addUser(user1);
        userService.addUser(user2);
        filmService.addLike(1, user1.getId());
        filmService.addLike(2, user1.getId());
        filmService.addLike(4, user1.getId());
        filmService.addLike(1, user2.getId());
        filmService.addLike(4, user2.getId());

        assertEquals(2, filmService.findCommonFilms(1, 2).size());
        assertTrue(filmService.findCommonFilms(1, 2).contains(filmStorage.getFilm(1)) &&
                filmService.findCommonFilms(1, 2).contains(filmStorage.getFilm(4)));
    }

}