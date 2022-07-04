package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class Feed {
    private final Integer eventId;
    private final Integer userId;
    private final Integer entityId;
    private final Event eventType;
    private final Operation operation;
    private final Long timestamp;
}