package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Like;

import java.util.Collection;

public interface LikeDao {
  Collection<Like> getAllLikes();
}
