package ru.yandex.practicum.filmorate.service;

import jakarta.validation.constraints.Positive;
import ru.yandex.practicum.filmorate.model.Film;
import java.util.List;

public interface FilmService {

    void addLike(Long filmId, Long userId);

    void removeLike(Long filmId, Long userId);

    List<Film> getTenPopularFilms(@Positive int count);

    List<Film> getAllFilms();

    Film getFilmById(Long id);

    Film createFilm(Film film);

    Film updateFilm(Film film);
}