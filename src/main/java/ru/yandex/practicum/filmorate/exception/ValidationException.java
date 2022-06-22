package ru.yandex.practicum.filmorate.exception;

public class ValidationException extends RuntimeException {

    public ValidationException(String s) {
        System.out.println(s);
    }
}