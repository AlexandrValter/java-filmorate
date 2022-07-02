package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class Feed {
    private final Long timestamp;
    private final Integer userId;
    private final Event eventType;
    private final Operation operation;
    private final Integer eventId;
    private final Integer entityId;
}