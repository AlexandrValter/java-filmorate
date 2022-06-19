package ru.yandex.practicum.filmorate.storage.dao;

import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Map;

@Component("UserDbStorage")
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User addUser(User user) {
        user.setName(user.getName());
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
        String sql = "SELECT * FROM users";
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
        if ((user.getId() != null) && (getUser(user.getId()) != null)) {
            user.setName(user.getName());
            String sql = "MERGE INTO users (id, login, email, name, birthday) " +
                    "KEY (id) VALUES (?, ?, ?, ?, ?)";
            jdbcTemplate.update(sql,
                    user.getId(),
                    user.getLogin(),
                    user.getEmail(),
                    user.getName(),
                    user.getBirthday());
        } else {
            addUser(user);
        }
        return user;
    }

    @Override
    public User getUser(int id) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("SELECT * FROM users WHERE id = ?", id);
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
    public void setName(User user) {
    }
}