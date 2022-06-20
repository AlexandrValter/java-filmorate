package ru.yandex.practicum.filmorate.storage.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.GenreDao;

import java.util.List;

@Slf4j
@Component
public class GenreDaoImpl implements GenreDao {

    private final JdbcTemplate jdbcTemplate;

    public GenreDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public List<Genre> getAllGenres() {
        String sql = "SELECT * FROM genres;";
        List<Genre> genres = jdbcTemplate.query(sql, (rs, rowNum) -> new Genre(
                rs.getInt("genre_id"),
                rs.getString("name"))
        );
        log.info("Запрошен список всех жанров");
        return genres;
    }

    @Override
    public Genre getGenre(int id) {
        SqlRowSet genreRows = jdbcTemplate.queryForRowSet(
                "SELECT * FROM genres WHERE genre_id=?;", id);
        if (genreRows.next()) {
            Genre genre = new Genre(
                    genreRows.getInt("genre_id"),
                    genreRows.getString("name")
            );
            log.info("Запрошен жанр id = {}", id);
            return genre;
        } else {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    String.format("Жанр с id %d не найден", id)
            );
        }
    }
}
