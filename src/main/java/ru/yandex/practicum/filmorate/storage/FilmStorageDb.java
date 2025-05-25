package ru.yandex.practicum.filmorate.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.filmorate.model.Film;

public interface FilmStorageDb extends JpaRepository<Film, Long> {
}