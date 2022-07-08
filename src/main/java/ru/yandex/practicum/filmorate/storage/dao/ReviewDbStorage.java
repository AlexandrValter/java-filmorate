package ru.yandex.practicum.filmorate.storage.dao;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ReviewDbStorage implements ReviewStorage {
    private final UserStorage userStorage;
    private final JdbcTemplate jdbcTemplate;
    private final static int NEW_USEFUL = 0;

    public ReviewDbStorage(@Qualifier("UserDbStorage") UserStorage userStorage,
                           JdbcTemplate jdbcTemplate) {
        this.userStorage = userStorage;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Review> getAllReview() {
        String sql = "SELECT * FROM reviews";
        return jdbcTemplate.query(sql, (this::getReview)).stream()
                .sorted((o1, o2) -> {
                    int result = o1.getUseful().compareTo(o2.getUseful());
                    return result * -1;
                })
                .collect(Collectors.toList());
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
        return findReviewById(review.getId());
    }

    @Override
    public Review findReviewById(Integer id) {
        String sql = "SELECT * FROM reviews WHERE id_review = ?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, id);
        if (rowSet.next()) {
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
            throw new NotFoundException("Review с id " + id + " не найден");
        }
    }

    @Override
    public List<Review> getAllReviewByIdFilm(Integer filmId, Integer count) {
        String sql = "SELECT * FROM reviews WHERE film_id = ?";
        return jdbcTemplate.query(sql, (this::getReview), filmId).stream()
                .sorted((o1, o2) -> {
                    int result = o1.getUseful().compareTo(o2.getUseful());
                    return result * -1;
                })
                .limit(count)
                .collect(Collectors.toList());
    }

    @Override
    public void addLikeDislike(Integer id, Integer userId, Integer value) {
        if (findReviewById(id) != null && userStorage.getUser(userId) != null) {
            String sqlQuery = "SELECT * FROM reviews_ratings WHERE user_id = ? AND id_review = ?";
            SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sqlQuery, userId, id);
            if (rowSet.next()) {
                String sqlUpdate = "UPDATE reviews_ratings SET rate = ? WHERE user_id = ? AND id_review = ?";
                jdbcTemplate.update(sqlUpdate, value, userId, id);
            } else {
                String sql = "INSERT INTO reviews_ratings (id_review, user_id, rate) VALUES (?,?,?)";
                jdbcTemplate.update(sql, id, userId, value);
            }
            changeUseful(id, value);
        } else {
            throw new NotFoundException("Отсутствует пользователь или отзыв");
        }
    }

    @Override
    public void deleteLikeDislike(Integer id, Integer userId, Integer value) {
        if (findReviewById(id) != null && userStorage.getUser(userId) != null) {
            changeUseful(id, value);
            String sql = "DELETE FROM reviews_ratings WHERE user_id = ? AND id_review = ?";
            jdbcTemplate.update(sql, userId, id);
        } else {
            throw new NotFoundException("Отсутствует пользователь или отзыв");
        }
    }

    private void changeUseful(Integer id, Integer num) {
        String sql = "UPDATE reviews SET useful = useful + ? WHERE id_review = ?";
        jdbcTemplate.update(sql, num, id);
    }

    private Review getReview(ResultSet rs, int rowNum) throws SQLException {
        Review review = new Review(rs.getString("content"),
                rs.getBoolean("is_positive"),
                rs.getInt("user_id"),
                rs.getInt("film_id"));
        review.setId(rs.getInt("id_review"));
        review.setUseful(rs.getInt("useful"));
        return review;
    }
}
