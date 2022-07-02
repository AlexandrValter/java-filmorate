package ru.yandex.practicum.filmorate.storage.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundReviewException;
import ru.yandex.practicum.filmorate.exception.ValidationReviewException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.review.Review;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ReviewDbStorage implements ReviewStorage {
    private final JdbcTemplate jdbcTemplate;
    private final FilmDbStorage filmDbStorage;
    private final UserDbStorage userDbStorage;
    private final static int NEW_USEFUL = 0;

    public ReviewDbStorage(JdbcTemplate jdbcTemplate, FilmDbStorage filmDbStorage, UserDbStorage userDbStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.filmDbStorage = filmDbStorage;
        this.userDbStorage = userDbStorage;
    }

    @Override
    public List<Review> getAllReview() {
        String sql = "SELECT * FROM reviews";
        return jdbcTemplate.query(sql, ((rs, rowNum) -> getReview(rs, rowNum))).stream()
                .sorted((o1, o2) -> {
                    int result = Integer.valueOf(o1.getUseful()).compareTo(Integer.valueOf(o2.getUseful()));
                    return result * -1;})
                .collect(Collectors.toList());
    }

    public static Review getReview(ResultSet rs, int rowNum) throws SQLException {
        Review review =  new Review(rs.getString("content"),
                rs.getBoolean("is_positive"),
                rs.getInt("user_id"),
                rs.getInt("film_id"));
        review.setId(rs.getInt("id_review"));
        review.setUseful(rs.getInt("useful"));
        return review;
    }

    @Override
    public Review addReview(Review review) {
            Map<String, Object> keys = new SimpleJdbcInsert(jdbcTemplate)
                    .withTableName("reviews")
                    .usingColumns("content", "is_positive", "user_id", "film_id", "useful")
                    .usingGeneratedKeyColumns("id_review")
                    .executeAndReturnKeyHolder(Map.of("content", review.getContent(),
                            "is_positive", review.getIsPositive(),
                            "user_id", review.getUserId(),
                            "film_id", review.getFilmId(),
                            "useful", NEW_USEFUL))
                    .getKeys();
            review.setId((Integer) keys.get("id_review"));
            return review;
    }

    @Override
    public void deleteReview(Integer id) {
        String sql = "DELETE FROM reviews WHERE id_review = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public Review changeReview(Review review) {
            String sql = "UPDATE reviews SET content = ?, is_positive = ? WHERE id_review = ?";
            jdbcTemplate.update(sql,
                    review.getContent(),
                    review.getIsPositive(),
                    review.getId()
            );
            return review;
    }

    @Override
    public Review findReviewById(Integer id) {
        String sql = "SELECT * FROM reviews WHERE id_review = ?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, id);
        if (rowSet.next()){
            Review review = new Review(
                    rowSet.getString("content"),
                    rowSet.getBoolean("is_positive"),
                    rowSet.getInt("user_id"),
                    rowSet.getInt("film_id")
            );
            review.setId(rowSet.getInt("id_review"));
            review.setUseful(rowSet.getInt("useful"));
            return review;
        } else {
            throw new NotFoundReviewException("Review с id " + id + " не найден");
        }
    }
}
