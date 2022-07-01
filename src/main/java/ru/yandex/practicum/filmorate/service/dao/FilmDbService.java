package ru.yandex.practicum.filmorate.service.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.*;

import java.util.*;

@Slf4j
@Service("FilmDbService")
public class FilmDbService implements FilmService {
    private final JdbcTemplate jdbcTemplate;
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final GenreDao genreDao;
    private final MpaDao mpaDao;

    private final DirectorDao directorDao;

    public FilmDbService(JdbcTemplate jdbcTemplate,
                         @Qualifier("FilmDbStorage") FilmStorage filmStorage,
                         @Qualifier("UserDbStorage") UserStorage userStorage,
                         GenreDao genreDao,
                         MpaDao mpaDao,
                         DirectorDao directorDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.genreDao = genreDao;
        this.mpaDao = mpaDao;
        this.directorDao = directorDao;
    }

    @Override
    public Film addFilm(Film film) {
        filmStorage.addFilm(film);
        fillingGenres(film);
        fillingDirectors(film);
        log.info("В базу добавлен новый фильм id = {}", film.getId());
        return film;
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
    public Film addOrUpdateFilm(Film film) {
        if (film.getId() != null && getFilm(film.getId()) != null) {
            filmStorage.addOrUpdateFilm(film);
            fillingGenres(film);
            fillingDirectors(film);
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
            log.info("Пользователь id={} удалил лайк с фильма id={}", userId, filmId);
        }
    }

    @Override
    public Film getFilm(int id) {
        Film film = filmStorage.getFilm(id);
        film.setMpa(getFilmMpa(id));
        film.setGenres(getFilmGenres(id));
        film.setDirectors(getFilmDirector(id));
        log.info("Запрошен фильм id = {}", film.getId());
        return film;
    }

    @Override
    public Collection<Film> getAllFilms() {
        Collection<Film> films = filmStorage.getAllFilms();
        films
                .forEach(film -> {film.setMpa(getFilmMpa(film.getId()));
                    film.setGenres(getFilmGenres(film.getId()));
                    film.setDirectors(getFilmDirector(film.getId()));});
        return films;
    }

    @Override
    public List<Film> popularFilms(int count) {
        List<Film> films = filmStorage.getPopularFilms(count);
        setMpaAndGenre(films);
        log.info("Запрошен список популярных фильмов, количество запрошенных фильмов = {}", count);
        return films;
    }

    @Override
    public List<Film> filmByDirector(Integer idDirector, String param) {
        if(directorDao.getDirector(idDirector) != null) {
            List<Film> films = filmStorage.getFilmsByDirector(idDirector, param);
            films.forEach(film -> {film.setMpa(getFilmMpa(film.getId()));
                film.setGenres(getFilmGenres(film.getId()));
                film.setDirectors(getFilmDirector(film.getId()));});
            log.info("Запрошены фильмы режиссера id={}", idDirector);
            return films;
        }
        return new ArrayList<>();
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

    private Set<Director> getFilmDirector(Integer id){
        String sql = "SELECT DFL.ID_DIRECTOR id_dir,D.NAME name FROM DIRECTORS_FILMS_LINK DFL "
        +"LEFT JOIN DIRECTORS D on D.ID_DIRECTOR = DFL.ID_DIRECTOR "
                + "WHERE  DFL.ID_FILM=?";
        List<Director> directors = jdbcTemplate.query(sql,(rs, rowNum) ->
                new Director(rs.getInt("id_dir"),rs.getString("name")),id);
        if(!directors.isEmpty()) {
            return new HashSet<>(directors);
        } else return new HashSet<>();
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

    private void fillingDirectors(Film film) {
        if (film.getDirectors() != null && !film.getDirectors().isEmpty()) {
            for (Director director : film.getDirectors()) {
                String sql = "MERGE INTO DIRECTORS_FILMS_LINK(ID_DIRECTOR, ID_FILM) "
                        + "KEY (ID_DIRECTOR,ID_FILM) VALUES ( ?,? )";
                jdbcTemplate.update(sql, director.getId(), film.getId());
            }
        }
    }

    private void setMpaAndGenre(Collection<Film> films) {
        if (!films.isEmpty()) {
            for (Film film : films) {
                film.setGenres(getFilmGenres(film.getId()));
                film.setMpa(getFilmMpa(film.getId()));
                film.setDirectors(getFilmDirector(film.getId()));
            }
        }
    }
}