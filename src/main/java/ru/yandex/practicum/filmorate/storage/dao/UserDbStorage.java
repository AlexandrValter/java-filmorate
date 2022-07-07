package ru.yandex.practicum.filmorate.storage.dao;

import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Component("UserDbStorage")
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User addUser(User user) {
        Map<String, Object> keys = new SimpleJdbcInsert(this.jdbcTemplate)
                .withTableName("users")
                .usingColumns("login", "email", "name", "birthday")
                .usingGeneratedKeyColumns("id")
                .executeAndReturnKeyHolder(Map.of("login", user.getLogin(),
                        "email", user.getEmail(),
                        "name", user.getName(),
                        "birthday", Date.valueOf(user.getBirthday())))
                .getKeys();
        user.setId((Integer) keys.get("id"));
        return user;
    }

    @Override
    public Collection<User> getAllUsers() {
        String sql = "SELECT * FROM users;";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new User(
                rs.getInt("id"),
                rs.getString("login"),
                rs.getString("email"),
                rs.getString("name"),
                LocalDate.parse(rs.getString("birthday")))
        );
    }

    @Override
    public User addOrUpdateUser(User user) {
        String sql = "MERGE INTO users (id, login, email, name, birthday) " +
                "KEY (id) VALUES (?, ?, ?, ?, ?);";
        jdbcTemplate.update(sql,
                user.getId(),
                user.getLogin(),
                user.getEmail(),
                user.getName(),
                user.getBirthday());
        return user;
    }

    @Override
    public User getUser(int id) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("SELECT * FROM users WHERE id = ?;", id);
        if (userRows.next()) {
            return new User(
                    userRows.getInt("id"),
                    userRows.getString("login"),
                    userRows.getString("email"),
                    userRows.getString("name"),
                    LocalDate.parse(userRows.getString("birthday")));
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    String.format("Пользователь с id %d не найден", id));
        }
    }

    @Override
    public Map<Integer, User> getUsers() {
        throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED);
    }

    @Override
    public void deleteUser(int id) {
        String userDeleteSql = "DELETE FROM USERS WHERE ID=?";
        jdbcTemplate.update(userDeleteSql, ps -> {
            ps.setInt(1, id);
        });
    }

    @Override
    public void addFriends(int userId, int friendId) {
        SqlRowSet friendRows = queryFriendship(userId, friendId);
        if (!friendRows.next()) {
            doNotBilateral(userId, friendId);
        } else {
            String sql = "MERGE INTO friendship KEY(from_user_id, to_user_id) VALUES (?, ?, ?);";
            jdbcTemplate.update(sql, userId, friendId, true);
            jdbcTemplate.update(sql, friendId, userId, true);
        }
    }

    @Override
    public void doNotBilateral(int userId, int friendId) {
        String sql = "MERGE INTO friendship KEY(from_user_id, to_user_id) VALUES (?, ?, ?);";
        jdbcTemplate.update(sql, userId, friendId, false);
    }

    @Override
    public Friendship getFriendship(int userId, int friendId) {
        SqlRowSet friendRows = queryFriendship(userId, friendId);
        if (friendRows.next()) {
            return new Friendship(
                    friendRows.getInt("from_user_id"),
                    friendRows.getInt("to_user_id"),
                    friendRows.getBoolean("is_bilateral"));
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    String.format("У пользователя с id %d нет в друзьях пользователя с id %d", userId, friendId));
        }
    }

    @Override
    public List<User> getFriends(int userId) {
        String sql = "SELECT fr.to_user_id, u.login, u.email, u.name, u.birthday " +
                "FROM users AS u " +
                "RIGHT OUTER JOIN friendship AS fr ON fr.to_user_id = u.id " +
                "WHERE fr.from_user_id = ?;";
        Collection<User> users = jdbcTemplate.query(sql, this::makeUser, userId);
        return List.copyOf(users);
    }

    @Override
    public List<User> getCommonFriends(int userId, int friendId) {
        String sql = "SELECT fr.to_user_id, u.login, u.email, u.name, u.birthday, COUNT (to_user_id) " +
                "FROM friendship AS fr " +
                "LEFT OUTER JOIN users AS u ON fr.to_user_id = u.id " +
                "WHERE from_user_id = ? OR from_user_id = ? " +
                "GROUP BY to_user_id " +
                "HAVING COUNT (to_user_id) > 1;";
        Collection<User> users = jdbcTemplate.query(sql, this::makeUser, userId, friendId);
        return List.copyOf(users);
    }

    @Override
    public void deleteFromFriends(int userId, int friendId) {
        String sql = "DELETE FROM friendship WHERE from_user_id = ? AND to_user_id = ?;";
        jdbcTemplate.update(sql, userId, friendId);
    }

    private SqlRowSet queryFriendship(int userId, int friendId) {
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