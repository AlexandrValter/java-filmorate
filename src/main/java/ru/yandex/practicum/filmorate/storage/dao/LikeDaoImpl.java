package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.storage.LikeDao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class LikeDaoImpl implements LikeDao {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Collection<Like> getAllLikes() {
        String sql = "SELECT * FROM LIKES";
        return jdbcTemplate.query(sql, (rs, rowNum) -> createLike(rs));
    }

    @Override
    public Set<Integer> getFilmLikes(int filmId) {
        String sql = "SELECT USER_ID FROM LIKES WHERE FILM_ID = ?";
        return Set.copyOf(jdbcTemplate.query(sql,(rs,rowNum)->rs.getInt("USER_ID"),filmId));

    }

    private Like createLike(ResultSet rs) throws SQLException {
        int userId = rs.getInt("USER_ID");
        int filmId = rs.getInt("FILM_ID");
        return new Like(userId, filmId);
    }
}
