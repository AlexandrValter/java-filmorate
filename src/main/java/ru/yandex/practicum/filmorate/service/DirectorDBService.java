package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorDao;

import java.util.List;

@Service
public class DirectorDBService implements DirectorService{
    private DirectorDao directorDao;


    public DirectorDBService(DirectorDao directorDao) {
        this.directorDao = directorDao;
    }
    @Override
    public Director getDirector(Integer id){
        return directorDao.getDirector(id);
    }
    @Override
    public List<Director> getAll(){
        return directorDao.getAll();
    }
    @Override
    public Director addDirector(Director director){
        return directorDao.addDirector(director);
    }
    @Override
    public Director updateDirector(Director director){
        return directorDao.updateDirector(director);
    }
    @Override
    public void removeDirector(Integer id){
        directorDao.removeDirector(id);
    }
}
