package ru.yandex.practicum.filmorate.storage;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.*;
import java.sql.Date;
import java.util.*;

@Component
public class FilmJdbcStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    public FilmJdbcStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Film> filmRowMapper = (rs, rowNum) -> {
        Film film = new Film();
        film.setId(rs.getLong("id"));
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));
        film.setReleaseDate(rs.getDate("release_date").toLocalDate());
        film.setDuration(rs.getInt("duration"));

        // MPA
        Integer mpaId = rs.getObject("mpa_id", Integer.class);
        if (mpaId != null) {
            Mpa mpa = new Mpa();
            mpa.setId(mpaId);
            mpa.setName(getMpaNameById(mpaId));
            film.setMpa(mpa);
        } else {
            film.setMpa(null);
        }

        // Жанры и лайки
        film.setGenres(getGenresForFilm(film.getId()));
        film.setLikes(getLikesForFilm(film.getId()));

        return film;
    };

    // Вспомогательный метод для получения имени MPA по ID (опционально)
    private String getMpaNameById(Integer mpaId) {
        String sql = "SELECT name FROM mpa WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, String.class, mpaId);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public Film createFilm(Film film) {
        String sql = "INSERT INTO films(name, description, release_date, duration, mpa_id) VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            setObject(ps, 5, film.getMpa().getId(), Types.INTEGER);
            return ps;
        }, keyHolder);

        Long generatedId = Objects.requireNonNull(keyHolder.getKey()).longValue();
        film.setId(generatedId);
        updateFilmGenres(film);
        updateFilmLikes(film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        String sql = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? WHERE id = ?";
        jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());

        updateFilmGenres(film);
        updateFilmLikes(film);

        return film;
    }

    @Override
    public List<Film> getFilms() {
        String sql = "SELECT * FROM films";
        return jdbcTemplate.query(sql, filmRowMapper);
    }

    @Override
    public Film getFilmById(Long id) {
        String sql = "SELECT * FROM films WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, filmRowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            throw new RuntimeException("Фильм с id " + id + " не найден");
        }
    }

    @Override
    public List<Film> getAllFilms() {
        String sql = "SELECT * FROM films";
        return jdbcTemplate.query(sql, filmRowMapper);
    }

    private final RowMapper<Mpa> mpaRowMapper = (rs, rowNum) -> {
        Mpa mpa = new Mpa();
        mpa.setId(rs.getInt("id"));
        mpa.setName(rs.getString("name"));
        return mpa;
    };

    public Optional<Mpa> getMpaById(Integer id) {
        String sql = "SELECT * FROM mpa WHERE id = ?";
        try {
            return Optional.of(jdbcTemplate.queryForObject(sql, mpaRowMapper, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    private void setObject(PreparedStatement ps, int index, Object value, int type) throws SQLException {
        if (value == null) {
            ps.setNull(index, type);
        } else {
            ps.setObject(index, value, type);
        }
    }

    private Set<Genre> getGenresForFilm(Long filmId) {
        String sql = "SELECT g.id, g.name FROM genres g JOIN film_genres fg ON g.id = fg.genre_id WHERE fg.film_id = ?";
        return new HashSet<>(jdbcTemplate.query(sql, (rs, row) -> {
            Genre genre = new Genre();
            genre.setId(rs.getInt("id"));
            genre.setName(rs.getString("name"));
            return genre;
        }, filmId));
    }

    private Set<Long> getLikesForFilm(Long filmId) {
        String sql = "SELECT user_id FROM film_likes WHERE film_id = ?";
        return new HashSet<>(jdbcTemplate.queryForList(sql, Long.class, filmId));
    }

    private void updateFilmGenres(Film film) {
        jdbcTemplate.update("DELETE FROM film_genres WHERE film_id = ?", film.getId());
        for (Genre genre : film.getGenres()) {
            jdbcTemplate.update("INSERT INTO film_genres(film_id, genre_id) VALUES (?, ?)", film.getId(), genre.getId());
        }
    }

    private void updateFilmLikes(Film film) {
        jdbcTemplate.update("DELETE FROM film_likes WHERE film_id = ?", film.getId());
        for (Long userId : film.getLikes()) {
            jdbcTemplate.update("INSERT INTO film_likes(film_id, user_id) VALUES (?, ?)", film.getId(), userId);
        }
    }

    @Override
    public boolean existsById(Long id) {
        String sql = "SELECT COUNT(*) FROM films WHERE id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }
}