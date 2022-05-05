package ru.yandex.practicum.filmorate.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class UserControllerTest {
    private UserController controller;

    @BeforeEach
    public void createController() {
        this.controller = new UserController();
    }

    @Test
    public void test1_addUserWithCorrectData() {
        assertTrue(controller.getUsers().isEmpty());
        controller.addUser(new User("yandex@yandex.ru", "login",
                LocalDate.of(2000, 1, 1)));
        assertEquals(1, controller.getUsers().size());
        controller.addUser(new User("@", "login", LocalDate.now()));
        assertEquals(2, controller.getUsers().size());
        assertEquals("login", controller.getUsers().get(1).getName());
    }

    @Test
    public void test2_addUserWithIncorrectData() {
        assertTrue(controller.getUsers().isEmpty());
        assertThrows(
                ValidationException.class,
                () -> controller.addUser(new User("yandexyandex.ru", "login",
                        LocalDate.of(2000, 1, 1)))
        );
        assertThrows(
                ValidationException.class,
                () -> controller.addUser(new User(" ", "login",
                        LocalDate.of(2000, 1, 1)))
        );
        assertThrows(
                ValidationException.class,
                () -> controller.addUser(new User("yandex@yandex.ru", "lo gin",
                        LocalDate.of(2000, 1, 1)))
        );
        assertThrows(
                ValidationException.class,
                () -> controller.addUser(new User("yandex@yandex.ru", "login",
                        LocalDate.now().plusDays(1)))
        );
        assertTrue(controller.getUsers().isEmpty());
    }
}