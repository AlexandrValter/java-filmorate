package ru.yandex.practicum.filmorate.storage.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorDao;

import java.util.ArrayList;
import java.util.List;

@Component
public class DirectorDaoImpl implements DirectorDao {
    private  final JdbcTemplate jdbcTemplate;

    public DirectorDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Director getDirector(Integer id) {
        if(id == null || id <= 0){
            throw new NotFoundException("id должен быть > 0");
        }
        String sql = "SELECT * FROM DIRECTORS WHERE ID_DIRECTOR=?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql,id);
        if(rowSet.next()){
            Director director = new Director();
            director.setId(id);
            director.setName(rowSet.getString("NAME"));
            return director;
        } else {
            return null;
        }
    }

    @Override
    public List<Director> getAll() {
        List<Director> directorList = new ArrayList<>();
        String sql = "SELECT * FROM DIRECTORS";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql);
        while (rowSet.next()){
            Director director = new Director();
            director.setId(rowSet.getInt("ID_DIRECTOR"));
            director.setName(rowSet.getString("NAME"));
            directorList.add(director);
        }
        return directorList;
    }

    @Override
    public Director addDirector(Director director) {
        String sql = "INSERT INTO DIRECTORS(NAME) VALUES ( ? )";
        jdbcTemplate.update(sql,director.getName());
        return director;
    }

    @Override
    public Director updateDirector(Director director) {
        String sql = "UPDATE DIRECTORS SET NAME=? WHERE ID_DIRECTOR=?";
        jdbcTemplate.update(sql,director.getName(),director.getId());
        return director;
    }

    @Override
    public void removeDirector(Integer id) {
        String sql = "DELETE FROM DIRECTORS WHERE ID_DIRECTOR=?";
        jdbcTemplate.update(sql,id);
    }
}
