package ru.yandex.practicum.filmorate.service.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.RecommendationHandler;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Slf4j
@Service("UserDbService")
public class UserDbService implements UserService {

    private final JdbcTemplate jdbcTemplate;
    private final UserStorage userStorage;
    private final RecommendationHandler recommendationHandler;

    @Autowired
    public UserDbService(JdbcTemplate jdbcTemplate,
                         @Qualifier("UserDbStorage") UserStorage userStorage,
                         RecommendationHandler recommendationHandler) {
        this.jdbcTemplate = jdbcTemplate;
        this.userStorage = userStorage;
        this.recommendationHandler = recommendationHandler;
    }

    @Override
    public User addUser(User user) {
        user.setName(user.getName());
        userStorage.addUser(user);
        log.info("Добавлен пользователь id = {}", user.getId());
        return user;
    }

    @Override
    public Collection<User> getAllUsers() {
        log.info("Запрошен список всех пользователей");
        return userStorage.getAllUsers();
    }

    @Override
    public User addOrUpdateUser(User user) {
        if ((user.getId() != null) && (getUser(user.getId()) != null)) {
            user.setName(user.getName());
            log.info("Обновлена информация о пользователе id = {}", user.getId());
            return userStorage.addOrUpdateUser(user);
        } else {
            return addUser(user);
        }
    }

    @Override
    public User getUser(int id) {
        log.info("Запрошена информация о пользователе id = {}", id);
        return userStorage.getUser(id);
    }

    @Override
    public void addFriends(int userId, int friendId) {
        if (userStorage.getUser(userId) != null && userStorage.getUser(friendId) != null) {
            log.info("Пользователь id={} добавляет в друзья пользователя id={}", userId, friendId);
            SqlRowSet friendRows = getFriendship(userId, friendId);
            if (!friendRows.next()) {
                doNotBilateral(userId, friendId);
            } else {
                String sql = "MERGE INTO friendship KEY(from_user_id, to_user_id) VALUES (?, ?, ?);";
                jdbcTemplate.update(sql, userId, friendId, true);
                jdbcTemplate.update(sql, friendId, userId, true);
            }
        }
    }

    @Override
    public void deleteFriends(int userId, int friendId) {
        if (userStorage.getUser(userId) != null && userStorage.getUser(friendId) != null) {
            SqlRowSet friendRows = getFriendship(userId, friendId);
            Friendship friendship;
            if (friendRows.next()) {
                friendship = new Friendship(
                        friendRows.getInt("from_user_id"),
                        friendRows.getInt("to_user_id"),
                        friendRows.getBoolean("is_bilateral"));
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                        String.format("У пользователя с id %d нет в друзьях пользователя с id %d", userId, friendId));
            }
            String sql = "DELETE FROM friendship WHERE from_user_id = ? AND to_user_id = ?;";
            jdbcTemplate.update(sql, userId, friendId);
            if (friendship.isBilateral()) {
                doNotBilateral(friendId, userId);
            }
            log.info("Пользователь id={} удалил из друзей пользователя id={}", userId, friendId);
        }
    }

    @Override
    public List<User> getFriends(int userId) {
        if (userStorage.getUser(userId) != null) {
            String sql = "SELECT fr.to_user_id, u.login, u.email, u.name, u.birthday " +
                    "FROM users AS u " +
                    "RIGHT OUTER JOIN friendship AS fr ON fr.to_user_id = u.id " +
                    "WHERE fr.from_user_id = ?;";
            Collection<User> users = jdbcTemplate.query(sql, this::makeUser, userId);
            log.info("Запрошен список друзей пользователя id={}", userId);
            return List.copyOf(users);
        }
        return null;
    }

    @Override
    public List<User> getCommonFriends(int userId, int friendId) {
        if (userStorage.getUser(userId) != null && userStorage.getUser(friendId) != null) {
            String sql = "SELECT fr.to_user_id, u.login, u.email, u.name, u.birthday, COUNT (to_user_id) " +
                    "FROM friendship AS fr " +
                    "LEFT OUTER JOIN users AS u ON fr.to_user_id = u.id " +
                    "WHERE from_user_id = ? OR from_user_id = ? " +
                    "GROUP BY to_user_id " +
                    "HAVING COUNT (to_user_id) > 1;";
            Collection<User> users = jdbcTemplate.query(sql, this::makeUser, userId, friendId);
            log.info("Запрошены общие друзья пользователей id = {} и id = {}", userId, friendId);
            return List.copyOf(users);
        }
        return null;
    }

    @Override
    public Set<Film> findRecommendation(int id) {
        return recommendationHandler.findRecommendation(id);
    }

    @Override
    public void deleteUser(int id) {
        userStorage.deleteUser(id);
    }

    private void doNotBilateral(int userId, int friendId) {
        String sql = "MERGE INTO friendship KEY(from_user_id, to_user_id) VALUES (?, ?, ?);";
        jdbcTemplate.update(sql, userId, friendId, false);
    }

    private SqlRowSet getFriendship(int userId, int friendId) {
        return jdbcTemplate.queryForRowSet(
                "SELECT * " +
                        "FROM friendship " +
                        "WHERE from_user_id = ? AND to_user_id = ?;",
                userId, friendId);
    }

    private User makeUser(ResultSet rs, int rowNum) {
        try {
            return new User(
                    rs.getInt("to_user_id"),
                    rs.getString("login"),
                    rs.getString("email"),
                    rs.getString("name"),
                    LocalDate.parse(rs.getString("birthday")));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}