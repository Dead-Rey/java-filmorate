package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmServiceDb;
import java.util.List;

@RestController
@RequestMapping("/films")
@AllArgsConstructor
@Validated
public class FilmController {
    private final FilmServiceDb filmService;

    @GetMapping
    public List<Film> getAllFilms() {
        return filmService.getAllFilms();
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        return filmService.createFilm(film);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        return filmService.updateFilm(film);
    }

    @GetMapping("{id}")
    public Film getFilmById(@PathVariable @Positive Long id) {
        return filmService.getFilmById(id);
    }

    @PutMapping("{id}/like/{userId}")
    public void likeFilm(@PathVariable @Positive Long id, @PathVariable @Positive Long userId) {
        filmService.addLike(id, userId);
    }

    @DeleteMapping("{id}/like/{userId}")
    public void removeLike(@PathVariable @Positive Long id, @PathVariable @Positive Long userId) {
        filmService.removeLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(defaultValue = "10") int count) {
        return filmService.getTenPopularFilms(count);
    }
}