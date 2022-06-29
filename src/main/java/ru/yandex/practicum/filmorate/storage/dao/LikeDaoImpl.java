package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.storage.LikeDao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

@Component
@RequiredArgsConstructor
public class LikeDaoImpl implements LikeDao {
   private final JdbcTemplate jdbcTemplate;
    @Override
    public Collection<Like> getAllLikes() {
        String sql = "SELECT * FROM LIKES";
        return jdbcTemplate.query(sql,(rs,rowNum)->createLike(rs));
    }
    private Like createLike(ResultSet rs) throws SQLException {
        int userId = rs.getInt("USER_ID");
        int filmId = rs.getInt("FILM_ID");
        return  new Like(userId,filmId);
    }
}
