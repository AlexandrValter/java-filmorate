package ru.yandex.practicum.filmorate.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.Duration;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class FilmControllerTest {

    private FilmController controller;


    @BeforeEach
    public void createController() {
        this.controller = new FilmController();
    }

    @Test
    public void test1_addFilmWithCorrectData() {
        assertTrue(controller.getFilms().isEmpty());
        controller.addFilm(new Film("Фильм 1", "Описание фильма 1",
                LocalDate.of(1990, 01, 01), Duration.ofHours(2)));
        controller.addFilm(new Film("2", "Описание этого фильма состоит из двухсот символов - такая " +
                "длина является граничной для описания фильма согласно требованиям ТЗ. Приходится придумывать что " +
                "добавить еще, чтобы набрать двести символов..",
                LocalDate.of(1895, 12, 28), Duration.ofSeconds(1)));
        assertEquals(2, controller.getFilms().size());
    }

    @Test
    public void test2_addFilmWithIncorrectData() {
        assertTrue(controller.getFilms().isEmpty());
        assertThrows(
                ValidationException.class,
                () -> controller.addFilm(new Film("Фильм 1", "Описание фильма 1",
                        LocalDate.of(1895, 12, 27), Duration.ofHours(2)))
        );
        assertTrue(controller.getFilms().isEmpty());
    }
}