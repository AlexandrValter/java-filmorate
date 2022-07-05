package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorDao;

import java.util.List;

@Service
@Slf4j
public class DirectorDBService implements DirectorService{
    private DirectorDao directorDao;


    public DirectorDBService(DirectorDao directorDao) {
        this.directorDao = directorDao;
    }
    @Override
    public Director getDirector(Integer id){
        log.info("запрошен режиссер id={}",id);
        return directorDao.getDirector(id);
    }
    @Override
    public List<Director> getAll(){
        log.info("запрошены все режиссеры");
        return directorDao.getAll();
    }
    @Override
    public Director addDirector(Director director){
        log.info("добавлен режиссер id={}",director.getId());
        return directorDao.addDirector(director);
    }
    @Override
    public Director updateDirector(Director director){
        log.info("обновлен режиссер id={}",director.getId());
        return directorDao.updateDirector(director);
    }
    @Override
    public void removeDirector(Integer id){
        directorDao.removeDirector(id);
        log.info("удален режиссер id={}",id);
    }
}
