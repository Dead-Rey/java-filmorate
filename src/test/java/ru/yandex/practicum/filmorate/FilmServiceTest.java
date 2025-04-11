package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class FilmServiceTest {

    private FilmService filmService;

    @BeforeEach
    void setUp() {
        filmService = new FilmService(new ArrayList<>());
    }

    @Test
    void testCreateFilm_AssignsIdAndAddsToList() {
        Film film = new Film(null, "Inception", "Mind-bending movie", LocalDate.of(2010, 7, 16), 148);

        Film created = filmService.createFilm(film);

        assertNotNull(created.getId());
        assertEquals("Inception", created.getName());
        assertEquals(1, filmService.getFilms().size());
    }

    @Test
    void testUpdateFilm_UpdatesFieldsCorrectly() {
        Film film = new Film(null, "Old Name", "Old Description", LocalDate.of(2000, 1, 1), 100);
        filmService.createFilm(film);

        Film updatedFilm = new Film(film.getId(), "New Name", "New Description", LocalDate.of(2001, 2, 2), 120);
        Film result = filmService.updateFilm(updatedFilm);

        assertEquals("New Name", result.getName());
        assertEquals("New Description", result.getDescription());
        assertEquals(120, result.getDuration());
        assertEquals(LocalDate.of(2001, 2, 2), result.getReleaseDate());
    }

    @Test
    void testUpdateFilm_NotFound_ThrowsException() {
        Film film = new Film(999L, "Non-existent", "Doesn't matter", LocalDate.of(1999, 1, 1), 90);

        Exception exception = assertThrows(NotFoundException.class, () -> filmService.updateFilm(film));

        assertEquals("Фильм не найден", exception.getMessage());
    }

    @Test
    void testCreateFilm_DoesNotOverrideGivenId() {
        Film film = new Film(42L, "Film with ID", "Testing", LocalDate.of(2020, 5, 5), 90);
        Film created = filmService.createFilm(film);

        assertEquals(42L, created.getId());
    }
}
