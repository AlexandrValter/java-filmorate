package ru.yandex.practicum.filmorate.storage.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.storage.FeedDao;

import java.util.List;

@Component
public class FeedDaoImpl implements FeedDao {

    private final JdbcTemplate jdbcTemplate;

    public FeedDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Feed> getFeeds(int id) {
        String sql = "SELECT * FROM feeds WHERE user_id = ?;";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Feed(
                rs.getInt("event_id"),
                rs.getInt("user_id"),
                rs.getInt("entity_id"),
                Event.valueOf(rs.getString("event_type")),
                Operation.valueOf(rs.getString("operation")),
                rs.getTimestamp("time").toInstant().toEpochMilli()),id);
    }

    @Override
    public void addFeed(int userId, Event event, Operation operation, int entityId) {
        String sql = "INSERT INTO feeds (user_id, event_type, operation, entity_id) VALUES (?, ?, ?, ?);";
        jdbcTemplate.update(sql, userId, event.toString(), operation.toString(), entityId);
    }
}