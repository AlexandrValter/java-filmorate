package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;
import ru.yandex.practicum.filmorate.storage.DirectorDao;

import java.util.List;

@RestController
@RequestMapping("/directors")
public class DirectorController {
    private DirectorService directorService;

    public DirectorController(DirectorService directorService) {
        this.directorService = directorService;
    }

    @GetMapping("/{id}")
    public Director getDirector(@PathVariable Integer id){
        return directorService.getDirector(id);
    }

    @GetMapping
    public List<Director> getAll(){
        return  directorService.getAll();
    }

    @DeleteMapping("/{id}")
    public void removeDirector(@PathVariable Integer id){
        directorService.removeDirector(id);
    }

    @PostMapping
    public Director addDirector(@RequestBody Director director){
        return directorService.addDirector(director);
    }

    @PutMapping
    public Director updateDirector(@RequestBody Director director){
        return directorService.updateDirector(director);
    }
}
