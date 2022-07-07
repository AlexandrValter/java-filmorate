package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface UserStorage {

    User addUser(User user);

    Collection<User> getAllUsers();

    User addOrUpdateUser(User user);

    User getUser(int id);

    Map<Integer, User> getUsers();

    void deleteUser(int id);

    void addFriends(int userId, int friendId);

    void doNotBilateral(int userId, int friendId);

    Friendship getFriendship(int userId, int friendId);

    List<User> getFriends(int userId);

    List<User> getCommonFriends(int userId, int friendId);

    void deleteFromFriends(int userId, int friendId);
}