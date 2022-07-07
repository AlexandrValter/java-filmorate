package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.FeedDao;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Slf4j
@Service("UserDbService")
public class UserDbService implements UserService {

    private final UserStorage userStorage;
    private final RecommendationHandler recommendationHandler;
    private final FeedDao feedDao;

    @Autowired
    public UserDbService(@Qualifier("UserDbStorage") UserStorage userStorage,
                         RecommendationHandler recommendationHandler, FeedDao feedDao) {
        this.userStorage = userStorage;
        this.recommendationHandler = recommendationHandler;
        this.feedDao = feedDao;
    }

    @Override
    public User addUser(User user) {
        user.setName(user.getName());
        userStorage.addUser(user);
        log.info("Добавлен пользователь id = {}", user.getId());
        return user;
    }

    @Override
    public Collection<User> getAllUsers() {
        log.info("Запрошен список всех пользователей");
        return userStorage.getAllUsers();
    }

    @Override
    public User addOrUpdateUser(User user) {
        if ((user.getId() != null) && (getUser(user.getId()) != null)) {
            user.setName(user.getName());
            log.info("Обновлена информация о пользователе id = {}", user.getId());
            return userStorage.addOrUpdateUser(user);
        } else {
            return addUser(user);
        }
    }

    @Override
    public User getUser(int id) {
        log.info("Запрошена информация о пользователе id = {}", id);
        return userStorage.getUser(id);
    }

    @Override
    public void addFriends(int userId, int friendId) {
        if (userStorage.getUser(userId) != null && userStorage.getUser(friendId) != null) {
            log.info("Пользователь id={} добавляет в друзья пользователя id={}", userId, friendId);
            feedDao.addFeed(userId, Event.FRIEND, Operation.ADD, friendId);
            userStorage.addFriends(userId, friendId);
        }
    }

    @Override
    public void deleteFriends(int userId, int friendId) {
        if (userStorage.getUser(userId) != null && userStorage.getUser(friendId) != null) {
            Friendship friendship = userStorage.getFriendship(userId, friendId);
            userStorage.deleteFromFriends(userId, friendId);
            if (friendship.isBilateral()) {
                userStorage.doNotBilateral(friendId, userId);
            }
            feedDao.addFeed(userId, Event.FRIEND, Operation.REMOVE, friendId);
            log.info("Пользователь id={} удалил из друзей пользователя id={}", userId, friendId);
        }
    }

    @Override
    public List<User> getFriends(int userId) {
        if (userStorage.getUser(userId) != null) {
            log.info("Запрошен список друзей пользователя id={}", userId);
            return userStorage.getFriends(userId);
        }
        return null;
    }

    @Override
    public List<User> getCommonFriends(int userId, int friendId) {
        if (userStorage.getUser(userId) != null && userStorage.getUser(friendId) != null) {
            log.info("Запрошены общие друзья пользователей id = {} и id = {}", userId, friendId);
            return userStorage.getCommonFriends(userId, friendId);
        }
        return null;
    }

    @Override
    public Set<Film> findRecommendation(int id) {
        log.info("Запрошены рекомендации для пользователя id = {}", id);
        return recommendationHandler.findRecommendation(id);
    }

    @Override
    public void deleteUser(int id) {
        userStorage.deleteUser(id);
    }

    @Override
    public List<Feed> getFeeds(int id) {
        if (userStorage.getUser(id) != null) {
            log.info("Запрошена лента событий пользователя id = {}", id);
            return feedDao.getFeeds(id);
        }
        return null;
    }
}