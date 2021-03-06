package ru.yandex.practicum.filmorate.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.SearchBy;
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
    public List<Film> getPopularFilms(@RequestParam(defaultValue = "10") int count,
                                      @RequestParam(defaultValue = "-1") int genreId,
                                      @RequestParam(defaultValue = "-1") int year) {
        return filmService.popularFilms(count, genreId, year);
    }

    @DeleteMapping("/{filmId}")
    public void deleteFilm(@PathVariable int filmId) {
        filmService.deleteFilm(filmId);
    }

    @GetMapping("/common")
    public List<Film> findCommonFilms(@RequestParam int userId, @RequestParam int friendId) {
        return filmService.findCommonFilms(userId, friendId);
    }

    @GetMapping("/director/{directorId}")
    public List<Film> getFilmByDirector(@PathVariable Integer directorId, @RequestParam String sortBy) {
        return filmService.filmByDirector(directorId, sortBy);
    }

    @GetMapping("/search")
    public List<Film> searchByNameFilmAndDirector(@RequestParam String query,
                                                  @RequestParam(required = false, name = "by")
                                                  List<SearchBy> by) {
        return filmService.searchByTitleOrDirector(query, by);
    }
}