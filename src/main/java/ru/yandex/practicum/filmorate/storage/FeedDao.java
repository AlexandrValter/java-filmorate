package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Operation;

import java.util.List;

public interface FeedDao {

    List<Feed> getFeeds(int id);

    void addFeed(int userId, Event event, Operation operation, int entityId);
}