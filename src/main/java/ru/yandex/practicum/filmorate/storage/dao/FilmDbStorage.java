package ru.yandex.practicum.filmorate.storage.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Map;

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
                .usingColumns("name", "description", "release_date", "duration")
                .usingGeneratedKeyColumns("id")
                .executeAndReturnKeyHolder(Map.of("name", film.getName(),
                        "description", film.getDescription(),
                        "release_date", Date.valueOf(film.getReleaseDate()),
                        "duration", film.getDuration()))
                .getKeys();
        film.setId((Integer) keys.get("id"));
        return film;
    }

    @Override
    public Map<Integer, Film> getFilms() {
        return null;
    }

    @Override
    public Film addOrUpdateFilm(Film film) {
        if (film.getId() != null) {
            String sql = "MERGE INTO films (id, name, description, release_date, duration) " +
                    "KEY (id) VALUES (?, ?, ?, ?, ?)";
            jdbcTemplate.update(sql,
                    film.getId(),
                    film.getName(),
                    film.getDescription(),
                    film.getReleaseDate(),
                    film.getDuration());
        } else {
            addFilm(film);
        }
        return film;
    }

    @Override
    public Film getFilm(int id) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("SELECT * FROM films WHERE id = ?", id);
        if (userRows.next()) {
            return new Film(
                    userRows.getInt("id"),
                    userRows.getString("name"),
                    userRows.getString("description"),
                    LocalDate.parse(userRows.getString("release_date")),
                    userRows.getInt("duration"));
        } else {
            return null;
        }
    }

    @Override
    public Collection<Film> getAllFilms() {
        String sql = "SELECT * FROM films";
        Collection<Film> films = jdbcTemplate.query(sql, (rs, rowNum) -> new Film(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("description"),
                LocalDate.parse(rs.getString("release_date")),
                rs.getInt("duration"))
        );
        return films;
    }
}