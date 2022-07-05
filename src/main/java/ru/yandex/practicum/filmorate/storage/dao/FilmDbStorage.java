package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.*;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component("FilmDbStorage")
@Primary
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;    private final LikeDao likeDao;
    private final MpaDao mpaDao;
    private final GenreDao genreDao;
    private final DirectorDao directorDao;


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
        return film;
    }

    @Override
    public Map<Integer, Film> getFilms() {
        throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED);
    }

    @Override
    public Film addOrUpdateFilm(Film film) {
        String sqlMerge = "MERGE INTO films (id, name, description, release_date, duration, id_mpa) " +
                "KEY (id) VALUES (?, ?, ?, ?, ?, ?);";
        jdbcTemplate.update(sqlMerge,
                film.getId(),
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId());
        String sqlDel = "DELETE FROM film_genre WHERE film_id = ?;" +
                "DELETE FROM DIRECTORS_FILMS_LINK WHERE ID_FILM = ?;";
        jdbcTemplate.update(sqlDel, film.getId(), film.getId());

        return film;
    }

    @Override
    public Film getFilm(int id) {
        String sql = "SELECT * FROM films WHERE id=?";
        return jdbcTemplate.query(sql, this::makeFilm, id).stream().findAny()
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,
                String.format("Фильм с id %d не найден", id)));
    }

    @Override
    public Collection<Film> getAllFilms() {
        String sql = "SELECT * FROM films;";
        return jdbcTemplate.query(sql, this::makeFilm);
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        String sql = "SELECT f.id, f.name, f.description, f.release_date, f.duration, COUNT (l.user_id) " +
                "FROM films AS f " +
                "LEFT JOIN likes AS l ON f.id = l.film_id " +
                "GROUP BY f.id " +
                "ORDER BY COUNT (l.user_id) DESC " +
                "LIMIT ?;";
        return jdbcTemplate.query(sql, this::makeFilm, count);
    }

    @Override
    public void deleteFilm(int filmId) {
        String sql = "DELETE FROM FILMS WHERE ID=?";
        jdbcTemplate.update(sql, ps -> {
            ps.setInt(1, filmId);
        });
    }

    @Override
    public List<Film> getPopularFilmsByYear(int count, int year) {
        String sql = "SELECT f.id, f.name, f.description, f.release_date, f.duration, COUNT (l.user_id) " +
                "FROM films AS f " +
                "LEFT JOIN likes AS l ON f.id = l.film_id " +
                "WHERE EXTRACT(YEAR FROM f.release_date) = ? " +
                "GROUP BY f.id " +
                "ORDER BY COUNT (l.user_id) DESC " +
                "LIMIT ?;";
        return jdbcTemplate.query(sql, this::makeFilm, year, count);
    }

    @Override
    public List<Film> getPopularFilmsByGenre(int count, int genreId) {
        String sql = "SELECT f.id, f.name, f.description, f.release_date, f.duration, COUNT (l.user_id) " +
                "FROM films AS f " +
                "LEFT JOIN likes AS l ON f.id = l.film_id " +
                "LEFT JOIN film_genre AS fg ON f.id = fg.film_id " +
                "WHERE fg.genre_id = ?" +
                "GROUP BY f.id " +
                "ORDER BY COUNT (l.user_id) DESC " +
                "LIMIT ?;";
        return jdbcTemplate.query(sql, this::makeFilm, genreId, count);
    }

    @Override
    public List<Film> getFilmsByDirector(Integer idDirector, String param) {
        String sql = "SELECT f.id, f.name, f.description, f.release_date, f.duration, COUNT (l.user_id) " +
                "FROM films AS f LEFT JOIN likes AS l ON f.id = l.film_id " +
                "WHERE f.ID IN (SELECT ID_FILM FROM DIRECTORS_FILMS_LINK WHERE ID_DIRECTOR=?)" +
                "GROUP BY f.id ";
        if (param.equals("likes")) {
            sql += "ORDER BY COUNT (l.user_id) DESC";
        } else if (param.equals("year")) {
            sql += "ORDER BY f.release_date";
        }
        return jdbcTemplate.query(sql, this::makeFilm, idDirector);
    }

    @Override
    public List<Film> getPopularFilmsByGenreAndYear(int count, int genreId, int year) {
        String sql = "SELECT f.id, f.name, f.description, f.release_date, f.duration, COUNT (l.user_id) " +
                "FROM films AS f " +
                "LEFT JOIN likes AS l ON f.id = l.film_id " +
                "LEFT JOIN film_genre AS fg ON f.id = fg.film_id " +
                "WHERE fg.genre_id = ? AND EXTRACT(YEAR FROM f.release_date) = ? " +
                "GROUP BY f.id " +
                "ORDER BY COUNT (l.user_id) DESC " +
                "LIMIT ?;";
        return jdbcTemplate.query(sql, this::makeFilm, genreId, year, count);
    }

    @Override
    public Set<Film> getUserLikedFilms(int userId) {
        String sqlUserLiked = "SELECT * FROM FILMS WHERE ID IN(SELECT FILM_ID FROM LIKES WHERE USER_ID =?) ";

        return Set.copyOf(jdbcTemplate.query(sqlUserLiked, this::makeFilm,userId));

    }


    private Film makeFilm(ResultSet rs, int rowNum) {
        try {
            Film film = new Film(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("description"),
                    LocalDate.parse(rs.getString("release_date")),
                    rs.getInt("duration"));
            film.setMpa(mpaDao.getFilmMpa(film.getId()));
            film.setGenres(genreDao.getFilmGenres(film.getId()));
            film.setLikes(likeDao.getFilmLikes(film.getId()));
            film.setRate(film.getLikes().size());
            film.setDirectors(directorDao.getFilmDirector(film.getId()));
            return film;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}