package ru.yandex.practicum.filmorate.service.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Slf4j
@Service("FilmDbService")
public class FilmDbService implements FilmService {
    private final JdbcTemplate jdbcTemplate;
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public FilmDbService(JdbcTemplate jdbcTemplate,
                         @Qualifier("FilmDbStorage") FilmStorage filmStorage,
                         @Qualifier("UserDbStorage") UserStorage userStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    @Override
    public void addLike(int filmId, int userId) {
        if (filmStorage.getFilm(filmId) != null && userStorage.getUser(userId) != null) {
            String sql = "MERGE INTO likes KEY(film_id, user_id) VALUES (?, ?);";
            jdbcTemplate.update(sql, filmId, userId);
            log.info("Пользователь id={} поставил лайк фильму id={}", userId, filmId);
        }
    }

    @Override
    public void deleteLike(int filmId, int userId) {
        if (filmStorage.getFilm(filmId) != null && userStorage.getUser(userId) != null) {
            String sql = "DELETE FROM likes WHERE film_id = ? AND user_id = ?;";
            jdbcTemplate.update(sql, filmId, userId);
            log.info("Пользователь id={} удалил лайк с фильма id={}", userId, filmId);
        }
    }

    @Override
    public List<Film> popularFilms(int count) {
        log.info("Запрошен список популярных фильмов, количество запрошенных фильмов = {}", count);
        return filmStorage.getPopularFilms(count);
    }
}