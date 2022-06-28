package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface UserService {

    User addUser(User user);

    Collection<User> getAllUsers();

    User addOrUpdateUser(User user);

    User getUser(int id);

    void addFriends(int userId, int friendId);

    void deleteFriends(int userId, int friendId);

    List<User> getFriends(int userId);

    List<User> getCommonFriends(int userId, int friendId);

    Set<Film> findRecommendation(int id);

    void deleteUser(int id);
}