package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.ByEnum;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.dao.FilmDbService;
import ru.yandex.practicum.filmorate.storage.DirectorDao;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class DirectorDbStorageTest {
    private final DirectorDao directorDao;
    private final FilmDbService filmDbService;

    private Director director1 = new Director(1,"Dir1");
    private Director director2 = new Director(2,"Dir2");
    private Director director3 = new Director(3,"Dir3");
    private Mpa mpa = new Mpa(1,"G");

    private Film film1 = new Film(1,
            "film1",
            "film1",
            LocalDate.of(1990, 1, 1),
            120);

    private Film film2 = new Film(2,
            "film2",
            "film2",
            LocalDate.of(2000, 1, 1),
            120);

    private Film film3 = new Film(3,
            "Same name dir",
            "description",
            LocalDate.of(2001, 2,3), 180);
    @Test
    public void addDirectorGetAllTest1(){
        assertTrue(directorDao.getAll().isEmpty());
        directorDao.addDirector(director1);
        directorDao.addDirector(director2);
        assertEquals(2,directorDao.getAll().size(),"expected 2");
    }

    @Test
    public void deleteDirectorTest1() {
        directorDao.addDirector(director1);
        directorDao.addDirector(director2);
        assertEquals(2,directorDao.getAll().size(),"expected 2");
        directorDao.removeDirector(2);
        assertEquals(1,directorDao.getAll().size(),"expected 1");
    }

    @Test
    public void getDirectorTest1(){
        directorDao.addDirector(director1);
        assertEquals(1,directorDao.getDirector(1).getId(),"expected 1");
    }

    @Test
    public void updateDirectorTest1(){
        directorDao.addDirector(director1);
        director2.setId(1);
        director2.setName("UpdDir");
        directorDao.updateDirector(director2);
        assertEquals("UpdDir",directorDao.getDirector(1).getName(),"expected name 'UpdDir'");
    }

    @Test
    public void setEmptyNameOfDirectorTest1(){
        director1.setName(null);
        assertThrows(NullPointerException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                directorDao.addDirector(director1);
            }
        });
    }

    @Test
    public void filmByDirectorTest1(){
        Set<Director> directors = new HashSet<>();
        directors.add(director3);
        film1.setDirectors(directors);
        film1.setMpa(mpa);
        directorDao.addDirector(director3);
        filmDbService.addFilm(film1);

        assertEquals(1,filmDbService.filmByDirector(1,"year").size(),"expected 1");
    }

    @Test
    public void searchByTitleOnlyTest() {
        Set<Director> directors = new HashSet<>();
        directors.add(director1);
        directors.add(director2);
        directors.add(director3);
        film1.setDirectors(directors);
        film2.setDirectors(directors);
        film3.setDirectors(directors);
        film1.setMpa(mpa);
        film2.setMpa(mpa);
        film3.setMpa(mpa);
        directorDao.addDirector(director1);
        directorDao.addDirector(director2);
        directorDao.addDirector(director3);
        filmDbService.addFilm(film1);
        filmDbService.addFilm(film2);
        filmDbService.addFilm(film3);
        assertEquals(2,filmDbService.searchByTitleOrDirector("film", List.of(ByEnum.title)).size(),
                "expected 2");
    }

    @Test
    public void searchByDirectorOnlyTest() {
        Set<Director> directors = new HashSet<>();
        directors.add(director1);
        directors.add(director2);
        directors.add(director3);
        film1.setDirectors(directors);
        film2.setDirectors(directors);
        film3.setDirectors(directors);
        film1.setMpa(mpa);
        film2.setMpa(mpa);
        film3.setMpa(mpa);
        directorDao.addDirector(director1);
        directorDao.addDirector(director2);
        directorDao.addDirector(director3);
        filmDbService.addFilm(film1);
        filmDbService.addFilm(film2);
        filmDbService.addFilm(film3);
        assertEquals(3,filmDbService.searchByTitleOrDirector("Dir", List.of(ByEnum.director)).size(),
                "expected 3");
    }
    @Test
    public void searchByTitleOrDirectorTest() {
        Set<Director> directors = new HashSet<>();
        directors.add(director1);
        directors.add(director2);
        directors.add(director3);
        film1.setDirectors(directors);
        film2.setDirectors(directors);
        film3.setDirectors(directors);
        film1.setMpa(mpa);
        film2.setMpa(mpa);
        film3.setMpa(mpa);
        directorDao.addDirector(director1);
        directorDao.addDirector(director2);
        directorDao.addDirector(director3);
        filmDbService.addFilm(film1);
        filmDbService.addFilm(film2);
        filmDbService.addFilm(film3);
        assertEquals(1,filmDbService.searchByTitleOrDirector("dir",
                        List.of(ByEnum.director, ByEnum.title)).size(),"expected 1");
    }
}
