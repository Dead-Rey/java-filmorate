package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeption.AlreadyExistsException;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.Set;


@Service
@AllArgsConstructor
@Getter
@Slf4j
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public void addLike(Long filmId, Long userId) {
        log.info("Попытка добавить лайк от пользователя с ID {} к фильму с ID {}", userId, filmId);

        Set<Long> likes = filmStorage.getFilmById(filmId).getLikes();

        if (likes.contains(userId)) {
            log.warn("Пользователь с ID {} уже поставил лайк фильму с ID {}", userId, filmId);
            throw new AlreadyExistsException("Этот пользователь уже поставил лайк");
        }

        if (!userStorage.findAllUsers().contains(userStorage.findUserById(userId))) {
            log.error("Не найден пользователь с ID {}", userId);
            throw new NotFoundException("Пользователя с id: " + userId + " не существует");
        }

        likes.add(userId);
        filmStorage.getFilmById(filmId).setLikes(likes);
        log.info("Лайк от пользователя с ID {} успешно добавлен к фильму с ID {}", userId, filmId);
    }

    public void removeLike(Long filmId, Long userId) {
        log.info("Попытка удалить лайк от пользователя с ID {} у фильма с ID {}", userId, filmId);

        Set<Long> likes = filmStorage.getFilmById(filmId).getLikes();

        if (!likes.contains(userId)) {
            log.warn("Пользователь с ID {} не ставил лайк фильму с ID {}", userId, filmId);
            throw new NotFoundException("Этот пользователь еще не поставил лайк");
        }

        if (!userStorage.findAllUsers().contains(userStorage.findUserById(userId))) {
            log.error("Не найден пользователь с ID {}", userId);
            throw new NotFoundException("Пользователя с id: " + userId + " не существует");
        }

        likes.remove(userId);
        filmStorage.getFilmById(filmId).setLikes(likes);
        log.info("Лайк от пользователя с ID {} успешно удалён у фильма с ID {}", userId, filmId);
    }

    public List<Film> getTenPopularFilms(int count) {
        log.info("Запрос на получение {} популярных фильмов", count);

        List<Film> films = filmStorage.getFilms();
        List<Film> popularFilms = films.stream()
                .sorted((f1, f2) -> Integer.compare(f2.getLikes().size(), f1.getLikes().size()))
                .limit(count)
                .toList();

        log.info("Возвращено {} популярных фильмов", popularFilms.size());
        return popularFilms;
    }
}