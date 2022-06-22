package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;

public interface UserService {

    User addUser(User user);

    Collection<User> getAllUsers();

    User addOrUpdateUser(User user);

    User getUser(int id);

    void addFriends(int userId, int friendId);

    void deleteFriends(int userId, int friendId);

    List<User> getFriends(int userId);

    List<User> getCommonFriends(int userId, int friendId);
}