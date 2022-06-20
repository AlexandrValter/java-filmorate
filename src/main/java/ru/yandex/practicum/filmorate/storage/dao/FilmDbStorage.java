package ru.yandex.practicum.filmorate.storage.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@Component("FilmDbStorage")
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film addFilm(Film film) {
        Map<String, Object> keys = new SimpleJdbcInsert(this.jdbcTemplate)
                .withTableName("films")
                .usingColumns("name", "description", "release_date", "duration", "id_mpa")
                .usingGeneratedKeyColumns("id")
                .executeAndReturnKeyHolder(Map.of("name", film.getName(),
                        "description", film.getDescription(),
                        "release_date", Date.valueOf(film.getReleaseDate()),
                        "duration", film.getDuration(),
                        "id_mpa", film.getMpa().getId()))
                .getKeys();
        film.setId((Integer) keys.get("id"));
        fillingGenres(film);
        log.info("В базу добавлен новый фильм id = {}", film.getId());
        return film;
    }

    @Override
    public Map<Integer, Film> getFilms() {
        throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED);
    }

    @Override
    public Film addOrUpdateFilm(Film film) {
        if (film.getId() != null && getFilm(film.getId()) != null) {
            String sqlMerge = "MERGE INTO films (id, name, description, release_date, duration, id_mpa) " +
                    "KEY (id) VALUES (?, ?, ?, ?, ?, ?);";
            jdbcTemplate.update(sqlMerge,
                    film.getId(),
                    film.getName(),
                    film.getDescription(),
                    film.getReleaseDate(),
                    film.getDuration(),
                    film.getMpa().getId());
            String sqlDel = "DELETE FROM film_genre WHERE film_id = ?;";
            jdbcTemplate.update(sqlDel, film.getId());
            fillingGenres(film);
            log.info("Обновлена информация о фильме id = {}", film.getId());
        } else {
            addFilm(film);
        }
        return film;
    }

    @Override
    public Film getFilm(int id) {
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("SELECT * FROM films WHERE id=?;", id);
        if (filmRows.next()) {
            Film film = new Film(
                    filmRows.getInt("id"),
                    filmRows.getString("name"),
                    filmRows.getString("description"),
                    LocalDate.parse(filmRows.getString("release_date")),
                    filmRows.getInt("duration"));
            film.setMpa(getFilmMpa(id));
            film.setGenres(getFilmGenres(id));
            log.info("Запрошен фильм id = {}", film.getId());
            return film;
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    String.format("Фильм с id %d не найден", id));
        }
    }

    @Override
    public Collection<Film> getAllFilms() {
        String sql = "SELECT * FROM films;";
        Collection<Film> films = jdbcTemplate.query(sql, this::makeFilm);
        setMpaAndGenre(films);
        log.info("Запрошен список всех фильмов");
        return films;
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        String sql = "SELECT f.id, f.name, f.description, f.release_date, f.duration, COUNT (l.user_id) " +
                "FROM films AS f " +
                "LEFT JOIN likes AS l ON f.id = l.film_id " +
                "GROUP BY f.id " +
                "ORDER BY COUNT (l.user_id) DESC " +
                "LIMIT ?;";
        List<Film> films = jdbcTemplate.query(sql, this::makeFilm, count);
        setMpaAndGenre(films);
        return films;
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
                // String sql = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";
                String sql = "MERGE INTO film_genre (film_id, genre_id) KEY (film_id, genre_id) VALUES (?, ?);";
                jdbcTemplate.update(sql, film.getId(), genre.getId());
            }
        }
    }

    private Film makeFilm(ResultSet rs, int rowNum) {
        try {
            return new Film(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("description"),
                    LocalDate.parse(rs.getString("release_date")),
                    rs.getInt("duration"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
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