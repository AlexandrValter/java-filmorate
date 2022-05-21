package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(InMemoryUserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void addFriends(int userId, int friendId) {
        if (userStorage.getUsers().containsKey(userId)) {
            if (userStorage.getUsers().containsKey(friendId)) {
                userStorage.getUser(userId).getFriends().add(friendId);
                userStorage.getUser(friendId).getFriends().add(userId);
                log.info("Пользователи {} и {} стали друзьями",
                        userStorage.getUser(userId).getEmail(),
                        userStorage.getUser(friendId).getEmail());
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                        String.format("Пользователь с id %d не найден", friendId));
            }
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    String.format("Пользователь с id %d не найден", userId));
        }
    }

    public void deleteFriends(int userId, int friendId) {
        if (userStorage.getUsers().containsKey(userId)) {
            if (userStorage.getUsers().containsKey(friendId)) {
                userStorage.getUser(userId).getFriends().remove(friendId);
                userStorage.getUser(friendId).getFriends().remove(userId);
                log.info("Пользователи {} и {} перестали быть друзьями",
                        userStorage.getUser(userId).getEmail(),
                        userStorage.getUser(friendId).getEmail());
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                        String.format("Пользователь с id %d не найден", friendId));
            }
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    String.format("Пользователь с id %d не найден", userId));
        }
    }

    public List<User> getFriends(int userId) {
        List<User> friends = new ArrayList<>();
        if (userStorage.getUsers().containsKey(userId)) {
            if (!userStorage.getUser(userId).getFriends().isEmpty()) {
                for (int id : userStorage.getUser(userId).getFriends()) {
                    friends.add(userStorage.getUser(id));
                }
                return friends;
            }
            return null;
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    String.format("Пользователь с id %d не найден", userId));
        }
    }

    public List<User> getCommonFriends(int userId, int friendId) {
        List<User> friends = new ArrayList<>();
        if (userStorage.getUsers().containsKey(userId)) {
            if (userStorage.getUsers().containsKey(friendId)) {
                Integer[] user = userStorage.getUser(userId).getFriends().toArray(new Integer[0]);
                Integer[] friend = userStorage.getUser(friendId).getFriends().toArray(new Integer[0]);
                for (int i = 0; i < user.length; i++) {
                    for (int j = 0; j < friend.length; j++) {
                        if (user[i].equals(friend[j])) {
                            friends.add(userStorage.getUser(user[i]));
                        }
                    }
                }
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                        String.format("Пользователь с id %d не найден", friendId));
            }
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    String.format("Пользователь с id %d не найден", userId));
        }
        return friends;
    }
}