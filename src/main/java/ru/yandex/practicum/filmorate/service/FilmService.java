package ru.yandex.practicum.filmorate.service;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

@Service
@AllArgsConstructor
@Getter
@Slf4j
public class FilmService {

    List<Film> films;
    private static Long idCounter = 1L;

    public Film createFilm(@Valid Film film) {
        log.info("Создание фильма: {}", film.getName());
        if (film.getId() == null) {
            film.setId(idCounter++);
        }
        films.add(film);
        log.info("Фильм успешно создан: : {}", film.getName());
        return film;
    }

    public Film updateFilm(@Valid Film film) {
        log.info("Обновление фильма с id: {}", film.getId());
        if (!films.contains(film)) {
            log.error("Фильм с id: {} не найден", film.getId());
            throw new NotFoundException("Фильм не найден");
        }

        Film oldFilm = films.get(films.indexOf(film));

        if (film.getDescription() != null && !film.getDescription().equals(oldFilm.getDescription())) {
            log.debug("Описание фильма изменено с '{}' на '{}'", oldFilm.getDescription(), film.getDescription());
            oldFilm.setDescription(film.getDescription());
        }
        if(!film.getName().equals(oldFilm.getName())) {
            log.debug("Название фильма изменено с '{}' на '{}'", oldFilm.getName(), film.getName());
            oldFilm.setName(film.getName());
        }
        if(film.getDuration() != oldFilm.getDuration()) {
            log.debug("Продолжительность фильма изменена с '{}' на '{}'", oldFilm.getDuration(), film.getDuration());
            oldFilm.setDuration(film.getDuration());
        }
        if(film.getReleaseDate() != null && !film.getReleaseDate().equals(oldFilm.getReleaseDate())) {
            log.debug("Дата релиза фильма изменена с '{}' на '{}'", oldFilm.getReleaseDate(), film.getReleaseDate());
            oldFilm.setReleaseDate(film.getReleaseDate());
        }
        log.info("Фильм с id: {} успешно обновлён", film.getId());
        return oldFilm;
    }
}
