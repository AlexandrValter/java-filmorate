package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorDao;

import java.util.List;

@RestController
@RequestMapping("/directors")
public class DirectorController {
    private DirectorDao directorDao;

    public DirectorController(DirectorDao directorDao) {
        this.directorDao = directorDao;
    }

    @GetMapping("/{id}")
    public Director getDirector(@PathVariable Integer id){
        return directorDao.getDirector(id);
    }

    @GetMapping
    public List<Director> getAll(){
        return  directorDao.getAll();
    }

    @DeleteMapping("/{id}")
    public void removeDirector(@PathVariable Integer id){
        directorDao.removeDirector(id);
    }

    @PostMapping
    public Director addDirector(@RequestBody Director director){
        return directorDao.addDirector(director);
    }

    @PutMapping
    public Director updateDirector(@RequestBody Director director){
        return directorDao.updateDirector(director);
    }
}
