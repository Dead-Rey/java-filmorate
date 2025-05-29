package ru.yandex.practicum.filmorate.storage;


import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Optional;


public interface FilmStorage {

     Film createFilm(Film film);

     Film updateFilm(Film film);

     List<Film> getFilms();

     Film getFilmById(Long id);

     boolean existsById(Long id); // добавлено

     List<Film> getAllFilms();


     Optional<Mpa> getMpaById(Integer id);
}