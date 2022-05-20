package ru.yandex.practicum.filmorate.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserStorage userStorage;
    private final UserService userService;

    @Autowired
    public UserController(InMemoryUserStorage userStorage, UserService userService) {
        this.userStorage = userStorage;
        this.userService = userService;
    }

    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
        return userStorage.addUser(user);
    }

    @GetMapping
    public Map<Integer, User> getUsers() {
        return userStorage.getUsers();
    }

    @PutMapping
    public User addOrUpdateUser(@Valid @RequestBody User user) {
        return userStorage.addOrUpdateUser(user);
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable int id) {
        return userStorage.getUser(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend (@PathVariable int id, @PathVariable int friendId){
        userService.addFriends(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend (@PathVariable int id, @PathVariable int friendId){
        userService.deleteFriends(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriends (@PathVariable int id){
        return userService.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends (@PathVariable int id, @PathVariable int otherId){
        return userService.getCommonFriends(id, otherId);
    }
}