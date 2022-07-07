package ru.yandex.practicum.filmorate.storage.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.DirectorDao;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class DirectorDaoImpl implements DirectorDao {
    private final JdbcTemplate jdbcTemplate;

    public DirectorDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Director getDirector(Integer id) {
        String sql = "SELECT * FROM DIRECTORS WHERE ID_DIRECTOR=?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, id);
        if (rowSet.next()) {
            Director director = new Director(id, rowSet.getString("NAME"));
            return director;
        } else {
            throw new NotFoundException("director not found");
        }
    }

    @Override
    public List<Director> getAll() {
        List<Director> directorList = new ArrayList<>();
        String sql = "SELECT * FROM DIRECTORS";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql);
        while (rowSet.next()) {
            Director director = new Director(
                    rowSet.getInt("ID_DIRECTOR"),
                    rowSet.getString("NAME"));
            directorList.add(director);
        }
        return directorList;
    }

    @Override
    public Director addDirector(Director director) {
        String sql = "INSERT INTO DIRECTORS(NAME) VALUES ( ? )";
        jdbcTemplate.update(sql, director.getName());
        sql = "SELECT MAX(ID_DIRECTOR) max FROM DIRECTORS";
        SqlRowSet maxId = jdbcTemplate.queryForRowSet(sql);
        if (maxId.next()) {
            director.setId(maxId.getInt("max"));
        }
        return director;
    }

    @Override
    public Director updateDirector(Director director) {
        SqlRowSet testId = jdbcTemplate.queryForRowSet("SELECT ID_DIRECTOR FROM DIRECTORS WHERE ID_DIRECTOR=?"
                , director.getId());
        if (testId.next()) {
            String sql = "UPDATE DIRECTORS SET NAME=? WHERE ID_DIRECTOR=?";
            jdbcTemplate.update(sql, director.getName(), director.getId());
            return director;
        } else {
            throw new NotFoundException("director not found");
        }
    }

    @Override
    public void removeDirector(Integer id) {
        String sql = "DELETE FROM DIRECTORS WHERE ID_DIRECTOR=?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public Set<Director> getFilmDirector(Integer id) {
        String sql = "SELECT DFL.ID_DIRECTOR id_dir,D.NAME name FROM DIRECTORS_FILMS_LINK DFL "
                + "LEFT JOIN DIRECTORS D on D.ID_DIRECTOR = DFL.ID_DIRECTOR "
                + "WHERE  DFL.ID_FILM=?";
        List<Director> directors = jdbcTemplate.query(sql, (rs, rowNum) ->
                new Director(rs.getInt("id_dir"), rs.getString("name")), id);
        if (!directors.isEmpty()) {
            return new HashSet<>(directors);
        } else return new HashSet<>();
    }

    @Override
    public void fillingDirectors(Film film) {
        if (film.getDirectors() != null && !film.getDirectors().isEmpty()) {
            for (Director director : film.getDirectors()) {
                String sql = "MERGE INTO DIRECTORS_FILMS_LINK(ID_DIRECTOR, ID_FILM) "
                        + "KEY (ID_DIRECTOR,ID_FILM) VALUES ( ?,? )";
                jdbcTemplate.update(sql, director.getId(), film.getId());
            }
        }
    }
}