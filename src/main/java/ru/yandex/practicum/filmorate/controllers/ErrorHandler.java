package ru.yandex.practicum.filmorate.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exception.*;

import java.util.List;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public List<String> handleValidationException(final ValidationException e) {
        return List.of("Введеная дата релиза должна быть позднее 1895-12-28");
    }

    @ExceptionHandler(NotFoundReviewException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ru.yandex.practicum.filmorate.model.ErrorResponse handleNotFoundReviewException(final NotFoundReviewException e){
        return new ru.yandex.practicum.filmorate.model.ErrorResponse(String.format("MessageException: " +
                e.getMessage() + "\n StackTrace: " + e.getStackTrace()));
    }

    @ExceptionHandler(ValidationReviewException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ru.yandex.practicum.filmorate.model.ErrorResponse handleValidationReviewException(final ValidationReviewException e){
        return new ru.yandex.practicum.filmorate.model.ErrorResponse(String.format("MessageException: " +
                e.getMessage() + "\n StackTrace: " + e.getStackTrace()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ru.yandex.practicum.filmorate.model.ErrorResponse handleThrowable(final MethodArgumentNotValidException e){
        return new ru.yandex.practicum.filmorate.model.ErrorResponse("Некоректные данные " + e.getStackTrace());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse dataNotFound(final NotFoundException e) {
        return new ErrorResponse(e.getMessage());
    }
}