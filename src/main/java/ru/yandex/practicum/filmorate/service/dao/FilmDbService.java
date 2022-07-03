package ru.yandex.practicum.filmorate.service.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.*;

import java.util.Collection;
import java.util.List;
import java.util.TreeSet;

@Slf4j
@Service("FilmDbService")
public class FilmDbService implements FilmService {
    private final JdbcTemplate jdbcTemplate;
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final GenreDao genreDao;
    private final MpaDao mpaDao;
    private final FeedDao feedDao;

    public FilmDbService(JdbcTemplate jdbcTemplate,
                         @Qualifier("FilmDbStorage") FilmStorage filmStorage,
                         @Qualifier("UserDbStorage") UserStorage userStorage,
                         GenreDao genreDao,
                         MpaDao mpaDao,
                         FeedDao feedDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.genreDao = genreDao;
        this.mpaDao = mpaDao;
        this.feedDao = feedDao;
    }

    @Override
    public Film addFilm(Film film) {
        filmStorage.addFilm(film);
        fillingGenres(film);
        log.info("В базу добавлен новый фильм id = {}", film.getId());
        return film;
    }

    @Override
    public void addLike(int filmId, int userId) {
        if (filmStorage.getFilm(filmId) != null && userStorage.getUser(userId) != null) {
            String sql = "MERGE INTO likes KEY(film_id, user_id) VALUES (?, ?);";
            jdbcTemplate.update(sql, filmId, userId);
            feedDao.addFeed(userId, Event.LIKE, Operation.ADD, filmId);
            log.info("Пользователь id={} поставил лайк фильму id={}", userId, filmId);
        }
    }

    @Override
    public Film addOrUpdateFilm(Film film) {
        if (film.getId() != null && getFilm(film.getId()) != null) {
            filmStorage.addOrUpdateFilm(film);
            fillingGenres(film);
            log.info("Обновлена информация о фильме id = {}", film.getId());
            return film;
        } else {
            addFilm(film);
        }
        return null;
    }

    @Override
    public void deleteLike(int filmId, int userId) {
        if (filmStorage.getFilm(filmId) != null && userStorage.getUser(userId) != null) {
            String sql = "DELETE FROM likes WHERE film_id = ? AND user_id = ?;";
            jdbcTemplate.update(sql, filmId, userId);
            feedDao.addFeed(userId, Event.LIKE, Operation.REMOVE, filmId);
            log.info("Пользователь id={} удалил лайк с фильма id={}", userId, filmId);
        }
    }

    @Override
    public Film getFilm(int id) {
        Film film = filmStorage.getFilm(id);
        film.setMpa(getFilmMpa(id));
        film.setGenres(getFilmGenres(id));
        log.info("Запрошен фильм id = {}", film.getId());
        return film;
    }

    @Override
    public Collection<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    @Override
    public List<Film> popularFilms(int count, int genreId, int year) {
        if (genreId == -1) {
            if (year == -1) {
                List<Film> films = filmStorage.getPopularFilms(count);
                setMpaAndGenre(films);
                log.info("Запрошен список популярных фильмов, количество запрошенных фильмов = {}", count);
                return films;
            } else {
                List<Film> films = filmStorage.getPopularFilmsByYear(count, year);
                setMpaAndGenre(films);
                log.info("Запрошен список популярных фильмов с годом релиза = {}, количество запрошенных фильмов = {}",
                        year, count);
                return films;
            }
        } else {
            if (year == -1) {
                List<Film> films = filmStorage.getPopularFilmsByGenre(count, genreId);
                setMpaAndGenre(films);
                log.info("Запрошен список популярных фильмов с жанром id = {}, количество запрошенных фильмов = {}",
                        genreId, count);
                return films;
            }
            List<Film> films = filmStorage.getPopularFilmsByGenreAndYear(count, genreId, year);
            log.info("Запрошен список популярных фильмов с жанром id = {}, годом релиза = {}, " +
                            "количество запрошенных фильмов = {}", genreId, year, count);
            setMpaAndGenre(films);
            return films;
        }
    }

    @Override
    public List<Genre> getAllGenres() {
        log.info("Запрошен список всех жанров");
        return genreDao.getAllGenres();
    }

    @Override
    public Genre getGenre(int id) {
        log.info("Запрошен жанр id = {}", id);
        return genreDao.getGenre(id);
    }

    private TreeSet<Genre> getFilmGenres(int id) {
        String sql = "SELECT fg.genre_id, g.name " +
                "FROM film_genre AS fg " +
                "LEFT OUTER JOIN genres AS g ON fg.genre_id = g.genre_id " +
                "WHERE fg.film_id=?;";
        List<Genre> genres = jdbcTemplate.query(sql, (rs, rowNum) ->
                new Genre(rs.getInt("genre_id"), rs.getString("name")), id);
        if (!genres.isEmpty()) {
            return new TreeSet<>(genres);
        } else {
            return null;
        }
    }

    @Override
    public List<Mpa> getAllMpa() {
        log.info("Запрошен список всех рейтингов");
        return mpaDao.getAllMpa();
    }

    @Override
    public Mpa getMpa(int id) {
        log.info("Запрошен рейтинг id = {}", id);
        return mpaDao.getMpa(id);
    }

    @Override
    public void deleteFilm(int filmId) {
        filmStorage.deleteFilm(filmId);
    }

    private Mpa getFilmMpa(int id) {
        SqlRowSet mpaRows = jdbcTemplate.queryForRowSet(
                "SELECT f.id_mpa, mr.meaning_mpa " +
                        "FROM films AS f " +
                        "LEFT OUTER JOIN mpa_rating AS mr ON f.id_mpa = mr.id_mpa_rating " +
                        "WHERE f.id=?;", id);
        if (mpaRows.next()) {
            Mpa mpa = new Mpa(mpaRows.getInt("id_mpa"), mpaRows.getString("meaning_mpa"));
            return mpa;
        } else {
            return null;
        }
    }

    private void fillingGenres(Film film) {
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            for (Genre genre : film.getGenres()) {
                String sql = "MERGE INTO film_genre (film_id, genre_id) KEY (film_id, genre_id) VALUES (?, ?);";
                jdbcTemplate.update(sql, film.getId(), genre.getId());
            }
        }
    }

    private void setMpaAndGenre(Collection<Film> films) {
        if (!films.isEmpty()) {
            for (Film film : films) {
                film.setGenres(getFilmGenres(film.getId()));
                film.setMpa(getFilmMpa(film.getId()));
            }
        }
    }
}