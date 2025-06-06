package ru.yandex.practicum.filmorate.service;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.exeption.AlreadyExistsException;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@AllArgsConstructor
@Slf4j
@Validated
public class FilmServiceDb implements FilmService {


    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final GenreStorage genreStorage;

    @Override
    public void addLike(Long filmId, Long userId) {
        log.info("Попытка добавить лайк от пользователя с ID {} к фильму с ID {}", userId, filmId);
        Film film = filmStorage.getFilmById(filmId);
        if (!userStorage.existsById(userId)) {
            log.error("Не найден пользователь с ID {}", userId);
            throw new NotFoundException("Пользователя с id: " + userId + " не существует");
        }
        if (film.getLikes().contains(userId)) {
            log.warn("Пользователь с ID {} уже поставил лайк фильму с ID {}", userId, filmId);
            throw new AlreadyExistsException("Пользователь уже поставил лайк");
        }
        film.getLikes().add(userId);
        filmStorage.updateFilm(film);
        log.info("Лайк от пользователя с ID {} успешно добавлен к фильму с ID {}", userId, filmId);
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        log.info("Попытка удалить лайк от пользователя с ID {} у фильма с ID {}", userId, filmId);
        Film film = filmStorage.getFilmById(filmId);
        if (!userStorage.existsById(userId)) {
            log.error("Не найден пользователь с ID {}", userId);
            throw new NotFoundException("Пользователя с id: " + userId + " не существует");
        }
        if (!film.getLikes().contains(userId)) {
            log.warn("Пользователь с ID {} не ставил лайк фильму с ID {}", userId, filmId);
            throw new NotFoundException("Лайк пользователя не найден у фильма");
        }
        film.getLikes().remove(userId);
        filmStorage.updateFilm(film);
        log.info("Лайк от пользователя с ID {} успешно удалён у фильма с ID {}", userId, filmId);
    }

    @Override
    public List<Film> getTenPopularFilms(@Positive int count) {
        log.info("Запрос на получение {} популярных фильмов", count);
        List<Film> films = filmStorage.getAllFilms();
        return films.stream()
                .sorted((f1, f2) -> Integer.compare(f2.getLikes().size(), f1.getLikes().size()))
                .limit(count)
                .toList();
    }

    @Override
    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    @Override
    public Film getFilmById(Long id) {
        return filmStorage.getFilmById(id);
    }

    @Override
    public Film createFilm(Film film) {
        if (film.getMpa() != null && film.getMpa().getId() != null) {
            Optional<Mpa> mpaOptional = filmStorage.getMpaById(film.getMpa().getId());
            if (mpaOptional.isEmpty()) {
                throw new NotFoundException("MPA с id " + film.getMpa().getId() + " не найден");
            }
        }
        Set<Genre> genres = film.getGenres();
        for (Genre genre : genres) {
            if (genreStorage.findById(genre.getId()).isEmpty()) {
                throw new NotFoundException("Жанр с id " + genre.getId() + " не найден");
            }
        }
        return filmStorage.createFilm(film);
    }

    @Override
    public Film updateFilm(Film film) {
        if (film.getId() == null || !filmStorage.existsById(film.getId())) {
            throw new NotFoundException("Фильм с id " + film.getId() + " не найден");
        }
        return filmStorage.updateFilm(film);
    }
}