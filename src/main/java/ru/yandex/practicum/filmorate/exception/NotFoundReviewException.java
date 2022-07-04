package ru.yandex.practicum.filmorate.exception;

public class NotFoundReviewException extends RuntimeException{
    public NotFoundReviewException(String message) {
        super(message);
    }
}
