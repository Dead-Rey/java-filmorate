package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.List;

@RestController
@RequestMapping(path = "/films")
@AllArgsConstructor
@Validated
public class FilmController {

    private final FilmStorage filmStorage;
    private final FilmService filmService;

    @GetMapping
    public List<Film> getAllFilms() {
        return filmStorage.getFilms();
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        return filmStorage.createFilm(film);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        return filmStorage.updateFilm(film);
    }

    @GetMapping("{id}")
    public Film getFilmById(@PathVariable @Positive Long id) {
        return filmStorage.getFilmById(id);
    }

    @PutMapping("{id}/like/{userId}")
    public Film likeFilm(@PathVariable @Positive Long id, @PathVariable @Positive Long userId) {
        filmService.addLike(id,userId);
        return filmStorage.getFilmById(id);
    }

    @DeleteMapping("{id}/like/{userId}")
    public Film removeLike(@PathVariable @Positive Long id, @PathVariable @Positive Long userId) {
        filmService.removeLike(id,userId);
        return filmStorage.getFilmById(id);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(defaultValue = "10") int count) {
        return filmService.getTenPopularFilms(count);
    }
}