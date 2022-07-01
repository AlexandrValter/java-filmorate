package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Like;

import java.util.Collection;
import java.util.Set;

public interface LikeDao {
  Collection<Like> getAllLikes();
  Set<Integer> getFilmLikes(int filmId);
}
