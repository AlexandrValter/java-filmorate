package ru.yandex.practicum.filmorate.storage.dao;

import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaDao;

import java.util.List;

@Component
public class MpaDaoImpl implements MpaDao {

    private final JdbcTemplate jdbcTemplate;

    public MpaDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Mpa> getAllMpa() {
        String sql = "SELECT * FROM mpa_rating;";
        List<Mpa> mpa = jdbcTemplate.query(sql, (rs, rowNum) -> new Mpa(
                rs.getInt("id_mpa_rating"),
                rs.getString("meaning_mpa"))
        );
        return mpa;
    }

    @Override
    public Mpa getMpa(int id) {
        SqlRowSet mpaRows = jdbcTemplate.queryForRowSet(
                "SELECT * FROM mpa_rating WHERE id_mpa_rating=?;", id);
        if (mpaRows.next()) {
            Mpa mpa = new Mpa(
                    mpaRows.getInt("id_mpa_rating"),
                    mpaRows.getString("meaning_mpa")
            );
            return mpa;
        } else {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    String.format("Рейтинг с id %d не найден", id)
            );
        }
    }
}