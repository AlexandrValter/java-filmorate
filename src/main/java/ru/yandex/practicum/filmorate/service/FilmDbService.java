package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.*;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service("FilmDbService")
public class FilmDbService implements FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final GenreDao genreDao;
    private final MpaDao mpaDao;
    private final FeedDao feedDao;
    private final DirectorDao directorDao;

    public FilmDbService(@Qualifier("FilmDbStorage") FilmStorage filmStorage,
                         @Qualifier("UserDbStorage") UserStorage userStorage,
                         GenreDao genreDao,
                         MpaDao mpaDao,
                         FeedDao feedDao,
                         DirectorDao directorDao) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.genreDao = genreDao;
        this.mpaDao = mpaDao;
        this.feedDao = feedDao;
        this.directorDao = directorDao;
    }

    @Override
    public Film addFilm(Film film) {
        filmStorage.addFilm(film);
        genreDao.fillingGenres(film);
        directorDao.fillingDirectors(film);
        log.info("В базу добавлен новый фильм id = {}", film.getId());
        return film;
    }

    @Override
    public void addLike(int filmId, int userId) {
        if (filmStorage.getFilm(filmId) != null && userStorage.getUser(userId) != null) {
            filmStorage.addLike(filmId, userId);
            feedDao.addFeed(userId, Event.LIKE, Operation.ADD, filmId);
            log.info("Пользователь id={} поставил лайк фильму id={}", userId, filmId);
        }
    }

    @Override
    public void deleteLike(int filmId, int userId) {
        if (filmStorage.getFilm(filmId) != null && userStorage.getUser(userId) != null) {
            feedDao.addFeed(userId, Event.LIKE, Operation.REMOVE, filmId);
            filmStorage.deleteLike(filmId, userId);
            log.info("Пользователь id={} удалил лайк с фильма id={}", userId, filmId);
        }
    }

    @Override
    public Film addOrUpdateFilm(Film film) {
        if (film.getId() != null && getFilm(film.getId()) != null) {
            filmStorage.addOrUpdateFilm(film);
            genreDao.fillingGenres(film);
            directorDao.fillingDirectors(film);
            log.info("Обновлена информация о фильме id = {}", film.getId());
            return film;
        } else {
            return addFilm(film);
        }
    }

    @Override
    public Film getFilm(int id) {
        Film film = filmStorage.getFilm(id);
        log.info("Запрошен фильм id = {}", film.getId());
        return film;
    }

    @Override
    public Collection<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    @Override
    public List<Film> popularFilms(int count, int genreId, int year) {
        if (genreId == -1) {
            if (year == -1) {
                List<Film> films = filmStorage.getPopularFilms(count);
                log.info("Запрошен список популярных фильмов, количество запрошенных фильмов = {}", count);
                return films;
            } else {
                List<Film> films = filmStorage.getPopularFilmsByYear(count, year);
                log.info("Запрошен список популярных фильмов с годом релиза = {}, количество запрошенных фильмов = {}",
                        year, count);
                return films;
            }
        } else {
            if (year == -1) {
                List<Film> films = filmStorage.getPopularFilmsByGenre(count, genreId);
                log.info("Запрошен список популярных фильмов с жанром id = {}, количество запрошенных фильмов = {}",
                        genreId, count);
                return films;
            }
            List<Film> films = filmStorage.getPopularFilmsByGenreAndYear(count, genreId, year);
            log.info("Запрошен список популярных фильмов с жанром id = {}, годом релиза = {}, " +
                    "количество запрошенных фильмов = {}", genreId, year, count);
            return films;
        }
    }

    @Override
    public List<Film> filmByDirector(Integer idDirector, String param) {
        if (directorDao.getDirector(idDirector) != null) {
            List<Film> films = filmStorage.getFilmsByDirector(idDirector, param);
            log.info("Запрошены фильмы режиссера id={}", idDirector);
            return films;
        }
        return new ArrayList<>();
    }

    @Override
    public List<Genre> getAllGenres() {
        log.info("Запрошен список всех жанров");
        return genreDao.getAllGenres();
    }

    @Override
    public Genre getGenre(int id) {
        log.info("Запрошен жанр id = {}", id);
        return genreDao.getGenre(id);
    }


    @Override
    public List<Mpa> getAllMpa() {
        log.info("Запрошен список всех рейтингов");
        return mpaDao.getAllMpa();
    }

    @Override
    public Mpa getMpa(int id) {
        log.info("Запрошен рейтинг id = {}", id);
        return mpaDao.getMpa(id);
    }

    @Override
    public void deleteFilm(int filmId) {
        filmStorage.deleteFilm(filmId);
    }

    @Override
    public List<Film> findCommonFilms(int userId, int friendId) {
        Set<Film> userFilms = filmStorage.getUserLikedFilms(userId);
        Set<Film> friendFilms = filmStorage.getUserLikedFilms(friendId);
        log.info("Запрошены общие фильмы пользователей id = {} и id = {}", userId, friendId);
        if (userFilms.size() >= friendFilms.size()) {
            return findCommonInSet(userFilms, friendFilms);
        } else {
            return findCommonInSet(friendFilms, userFilms);
        }
    }

    @Override
    public List<Film> searchByTitleOrDirector(String query, List<SearchBy> by) {
        if (by.contains(SearchBy.title) && !by.contains(SearchBy.director)) {
            log.info("Запрошен поиск по тексту {} среди названий фильмов", query);
            return filmStorage.searchByTitle(query);
        } else if (by.contains(SearchBy.director) && !by.contains(SearchBy.title)) {
            log.info("Запрошен поиск по тексту {} среди режиссеров фильмов", query);
            return filmStorage.searchByDirector(query);
        } else if (by.contains(SearchBy.title) && by.contains(SearchBy.director)) {
            log.info("Запрошен поиск по тексту {} среди названий и режиссеров фильмов", query);
            return filmStorage.searchByTitleAndDirector(query);
        }
        return null;
    }

    private List<Film> findCommonInSet(Set<Film> set1, Set<Film> set2) {
        List<Film> commonList = new ArrayList<>();
        commonList = set1.stream().filter(set2::contains).collect(Collectors.toList());

        Collections.sort(commonList, new Comparator<Film>() {
            @Override
            public int compare(Film o1, Film o2) {
                return o1.getRate() - o2.getRate();
            }
        });
        return commonList;
    }
}