package ru.yandex.practicum.filmorate.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(@Qualifier("FilmDbService") FilmService filmService) {
        this.filmService = filmService;
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        return filmService.addFilm(film);
    }

    @GetMapping
    public Collection<Film> getFilms() {
        return filmService.getAllFilms();
    }

    @PutMapping
    public Film addOrUpdateFilm(@Valid @RequestBody Film film) {
        return filmService.addOrUpdateFilm(film);
    }

    @GetMapping("/{id}")
    public Film getFilm(@PathVariable int id) {
        return filmService.getFilm(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable int id, @PathVariable int userId) {
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable int id, @PathVariable int userId) {
        filmService.deleteLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(defaultValue = "10") int count) {
        return filmService.popularFilms(count);
    }

    @DeleteMapping("/{filmId}")
    public void deleteFilm(@PathVariable int filmId){
        filmService.deleteFilm(filmId);
    }

}