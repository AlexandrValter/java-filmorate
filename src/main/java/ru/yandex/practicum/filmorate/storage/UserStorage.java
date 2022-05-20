package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Map;

public interface UserStorage {

    User addUser(User user);

    Map<Integer, User> getUsers();

    User addOrUpdateUser(User user);

    User getUser(int id);
}
