package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.LikeDao;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Primary
public class RecommendationHandlerWithOutMarksImpl implements RecommendationHandler {
    private final LikeDao likeDao;

    private final FilmService filmService;

    public RecommendationHandlerWithOutMarksImpl(LikeDao likeDao,
                                                 @Qualifier("FilmDbService") FilmService filmService) {
        this.likeDao = likeDao;
        this.filmService = filmService;
    }

    @Override
    public Set<Film> findRecommendation(int userId) {
        Map<Integer, Set<Integer>> data = initializeData();
        Map<Integer, Integer> similarsMap = getSimilarMap(userId, data);
        Set<Integer> userFilmSet = data.get(userId);
        Set<Film> recomSet = new HashSet<>();
        AtomicInteger count = new AtomicInteger(0);
        similarsMap.keySet()
                .forEach(u -> {
                    if (similarsMap.get(u) >= count.get()) {
                        data.get(u).stream()
                                .filter(f -> !userFilmSet.contains(f))
                                .map(filmService::getFilm)
                                .forEach(recomSet::add);
                        count.set(similarsMap.get(u));
                    }
                });
        return recomSet;
    }

    private Map<Integer, Integer> getSimilarMap(int userId, Map<Integer, Set<Integer>> data) {
        Map<Integer, Integer> similarLikesMap = new HashMap<>();
        Set<Integer> userLikesSet = data.get(userId);
        data.keySet().stream()
                .filter(user -> !similarLikesMap.containsKey(user) && user != userId).forEach(user -> {
                    Integer similar = (int)data.get(user).stream()
                            .filter(userLikesSet::contains)
                            .count();
                    similarLikesMap.put(user, similar);
                });

        return similarLikesMap;
    }

    private Map<Integer, Set<Integer>> initializeData() {
        Map<Integer, Set<Integer>> data = new HashMap<>();
        likeDao.getAllLikes().forEach(like -> {
            if (!data.containsKey(like.getUserId())) {
                data.put(like.getUserId(), new HashSet<>());
            }
            data.get(like.getUserId()).add(like.getFilmId());
        });
        return data;
    }
}