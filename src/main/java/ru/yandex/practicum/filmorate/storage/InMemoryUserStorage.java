package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Integer, User> users = new HashMap<>();
    private Integer id;

    @Override
    public User addUser(User user) {
        setName(user);
        user.setId(makeId());
        users.put(user.getId(), user);
        log.info("Добавлен новый пользователь {}, id={}", user.getName(), user.getId());
        return user;
    }

    @Override
    public Map<Integer, User> getUsers() {
        return users;
    }


    @Override
    public User addOrUpdateUser(User user) {
        if (user.getId() != null) {
            if (users.containsKey(user.getId())) {
                setName(user);
                users.put(user.getId(), user);
                log.info("Обновлена информация о пользователе {}, id={}", user.getName(), user.getId());
                return user;
            }
            return null;
        } else {
            setName(user);
            user.setId(makeId());
            users.put(user.getId(), user);
            log.info("Добавлен новый пользователь {}, id={}", user.getName(), user.getId());
            return user;
        }
    }

    @Override
    public User getUser(int id){
        if (users.containsKey(id)) {
            return users.get(id);
        }
        return null;
    }

    @Override
    public void deleteUser(int id) {
        throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED);
    }

    private void setName(User user) {
        if (user.getName() == null) {
            log.warn("Имя не введено");
            user.setName(user.getLogin());
        } else if (user.getName().isBlank()) {
            log.warn("Введено пустое имя");
            user.setName(user.getLogin());
        }
    }

    private Integer makeId() {
        if (id == null) {
            id = 1;
        } else {
            id++;
        }
        return id;
    }
}
