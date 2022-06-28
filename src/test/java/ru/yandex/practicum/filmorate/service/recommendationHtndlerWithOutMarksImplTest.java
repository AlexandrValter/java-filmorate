package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest         // TODO: 28.06.2022  переделать под тестовую базу
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class recommendationHtndlerWithOutMarksImplTest {

    private final RecommendationHtndler rh;

    @Test
    public void fimdRecommendation() {
        rh.findRecommendation(1);
    }
}