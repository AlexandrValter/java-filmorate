package ru.yandex.practicum.filmorate.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;

@RestController
@RequestMapping("/mpa")
public class MpaController {
    private final FilmService filmService;

    @Autowired
    public MpaController(@Qualifier("FilmDbService") FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public Collection<Mpa> getAllMpa() {
        return filmService.getAllMpa();
    }

    @GetMapping("/{id}")
    public Mpa getMpa(@PathVariable int id) {
        return filmService.getMpa(id);
    }
}