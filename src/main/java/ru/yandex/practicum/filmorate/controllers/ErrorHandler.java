package ru.yandex.practicum.filmorate.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exception.NotFoundReviewException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.exception.ValidationReviewException;
import ru.yandex.practicum.filmorate.model.ErrorResponse;

import java.util.List;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public List<String> handleValidationException(final ValidationException e) {
        return List.of("Введеная дата релиза должна быть позднее 1895-12-28");
    }

    @ExceptionHandler(NotFoundReviewException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundReviewException(final NotFoundReviewException e){
        return new ErrorResponse(String.format("MessageException: " +
                e.getMessage() + "\n StackTrace: " + e.getStackTrace()));
    }

    @ExceptionHandler(ValidationReviewException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationReviewException(final ValidationReviewException e){
        return new ErrorResponse(String.format("MessageException: " +
                e.getMessage() + "\n StackTrace: " + e.getStackTrace()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleThrowable(final MethodArgumentNotValidException e){
        return new ErrorResponse("Некоректные данные " + e.getStackTrace());
    }

}
