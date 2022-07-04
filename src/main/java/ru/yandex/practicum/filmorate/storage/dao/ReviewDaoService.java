package ru.yandex.practicum.filmorate.storage.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundReviewException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.review.Review;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;

import java.util.List;
import java.util.stream.Collectors;

import static ru.yandex.practicum.filmorate.storage.dao.ReviewDbStorage.getReview;
@Slf4j
@Component
public class ReviewDaoService {
    private final ReviewStorage reviewStorage;
    private final UserDbStorage userDbStorage;
    private final JdbcTemplate jdbcTemplate;

    public ReviewDaoService(ReviewStorage reviewStorage, JdbcTemplate jdbcTemplate, UserDbStorage userDbStorage) {
        this.reviewStorage = reviewStorage;
        this.jdbcTemplate = jdbcTemplate;
        this.userDbStorage = userDbStorage;
    }

    public List<Review> getAllReviewByIdFilm(Integer filmId, Integer count){
        String sql = "SELECT * FROM reviews WHERE film_id = ?";
        return jdbcTemplate.query(sql, ((rs, rowNum) -> getReview(rs, rowNum)), filmId).stream()
                .sorted((o1, o2) -> {
                    int result = Integer.valueOf(o1.getUseful()).compareTo(Integer.valueOf(o2.getUseful()));
                    return result * -1;})
                .limit(count)
                .collect(Collectors.toList());
    }

    public void changeUseful(Integer id, Integer num){
        String sql = "UPDATE reviews SET useful = useful + ? WHERE id_review = ?";
        jdbcTemplate.update(sql, num, id);
    }

    public void addLikeDislike(Integer id, Integer userId, Integer value){
        if (reviewStorage.findReviewById(id) instanceof Review &&
                userDbStorage.getUser(userId) instanceof User){
            String sqlQuery = "SELECT * FROM reviews_ratings WHERE user_id = ? AND id_review = ?";
            SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sqlQuery, userId, id);
            if (rowSet.next()){
                String sqlUpdate = "UPDATE reviews_ratings SET rate = ? WHERE user_id = ? AND id_review = ?";
                jdbcTemplate.update(sqlUpdate, value, userId, id);
            } else {
                String sql = "INSERT INTO reviews_ratings (id_review, user_id, rate) VALUES (?,?,?)";
                jdbcTemplate.update(sql, id, userId, value);
            }
            changeUseful(id, value);
        } else {
            throw new NotFoundReviewException("Отсутствует пользователь или отзыв");
        }
    }

    public void deleteLikeDislike(Integer id, Integer userId, Integer value){
        if (reviewStorage.findReviewById(id) instanceof Review &&
                userDbStorage.getUser(userId) instanceof User){
            changeUseful(id, value);
            String sql = "DELETE FROM reviews_ratings WHERE user_id = ? AND id_review = ?";
            jdbcTemplate.update(sql, userId, id);
        } else {
            throw new NotFoundReviewException("Отсутствует пользователь или отзыв");
        }
    }
}
