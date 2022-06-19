package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Map;

public interface UserStorage {

    User addUser(User user);

    Collection<User> getAllUsers();

    User addOrUpdateUser(User user);

    User getUser(int id);

    Map<Integer, User> getUsers();

    void setName(User user);
}