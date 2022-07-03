package ru.yandex.practicum.filmorate.storage.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorDao;

import javax.xml.bind.ValidationException;
import java.util.ArrayList;
import java.util.List;

@Component
public class DirectorDaoImpl implements DirectorDao {
    private final JdbcTemplate jdbcTemplate;

    public DirectorDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Director getDirector(Integer id) {
        if (id == null || id <= 0) {
            throw new NotFoundException("id должен быть > 0");
        }
        String sql = "SELECT * FROM DIRECTORS WHERE ID_DIRECTOR=?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, id);
        if (rowSet.next()) {
            Director director = new Director(id,rowSet.getString("NAME"));
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
            Director director = new Director(rowSet.getInt("ID_DIRECTOR"),rowSet.getString("NAME"));
            directorList.add(director);
        }
        return directorList;
    }

    @Override
    public Director addDirector(Director director) {
        if(director.getName().equals(" ")||director.getName().equals("") || director.getName().equals(null)){
            try {
                throw new ValidationException("неправильный формат имени");
            } catch (ValidationException e) {
                throw new RuntimeException(e);
            }
        }
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
                ,director.getId());
        if(testId.next()) {
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
}