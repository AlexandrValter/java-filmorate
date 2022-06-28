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

@Component
@Primary

public class RecommendationHtndlerWithOutMarksImpl implements RecommendationHtndler {
    public static final float SIMILAR_PERCENT = 0.5f;
    private final LikeDao likeDao;

    private final FilmService filmService;

    public RecommendationHtndlerWithOutMarksImpl(LikeDao likeDao, @Qualifier("FilmDbService") FilmService filmService) {
        this.likeDao = likeDao;
        this.filmService = filmService;
    }

    @Override
    public Set<Film> findRecommendation(int userId) {
        Map<Integer, Set<Integer>> data = initializeData(); // TODO: 28.06.2022  проверить есть ли user
        Map<Integer, Float> similarsPercentMap = getSimilarMap(userId, data);
        Set<Integer> userFilmSet = data.get(userId);
        Set<Film> recomSet = new HashSet<>();
        similarsPercentMap.keySet()
                .forEach(u -> {
                    if (similarsPercentMap.get(u) >= SIMILAR_PERCENT) {
                        data.get(u).stream()
                                .filter(f -> !userFilmSet.contains(f))
                                .map(filmService::getFilm)
                                .forEach(recomSet::add);
                    }
                });
        return recomSet;
    }

    private Map<Integer, Float> getSimilarMap(int userId, Map<Integer, Set<Integer>> data) {
        Map<Integer, Float> similarLikesMap = new HashMap<>();
        Set userLikesSet = data.get(userId);
        data.keySet().stream()
                .filter(user -> !similarLikesMap.containsKey(user) && user != userId).forEach(user -> {
                    float similar = data.get(user).stream()
                            .filter(userLikesSet::contains)
                            .count() / (float) userLikesSet.size();
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
