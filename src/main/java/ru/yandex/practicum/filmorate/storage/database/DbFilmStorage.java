package ru.yandex.practicum.filmorate.storage.database;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.database.mappers.FilmRowMapper;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
@Qualifier("dbFilmStorage")
public class DbFilmStorage implements FilmStorage {
    private final JdbcTemplate jdbc;
    private final FilmRowMapper mapper;

    @Override
    public Collection<Film> getAll() {
        String query = "SELECT * FROM films";
        return jdbc.query(query, mapper).stream()
                .peek(this::setGenres)
                .peek(this::setLikes)
                .peek(this::setGenreName)
                .peek(this::setMpaName)
                .collect(Collectors.toList());
    }

    @Override
    public Film create(Film film) {
        String query = "INSERT INTO films(name, description, release_date, duration, rating_id)" +
                " VALUES (?, ?, ?, ?, ?)";
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            ps.setObject(1, film.getName());
            ps.setObject(2, film.getDescription());
            ps.setObject(3, film.getReleaseDate());
            ps.setObject(4, film.getDuration());
            ps.setObject(5, film.getMpa().getId());
            return ps;
        }, keyHolder);
        Long id = keyHolder.getKeyAs(Long.class);
        if (id != null) {
            film.setId(id);
            film.setGenres(new TreeSet<>(film.getGenres()));
            setGenreName(film);
            setMpaName(film);
            insertFilmGenres(film);
            return film;
        } else {
            throw new InternalServerException("Не удалось создать новый фильм");
        }
    }

    @Override
    public Film update(Film newFilm) {
        String query = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, rating_id = ? " +
                " WHERE film_id = ?";
        int rowsUpdated = jdbc.update(query,
                newFilm.getName(),
                newFilm.getDescription(),
                newFilm.getReleaseDate(),
                newFilm.getDuration(),
                newFilm.getMpa().getId(),
                newFilm.getId()
        );
        if (rowsUpdated == 0) {
            throw new InternalServerException("Не удалось обновить данные пользователя");
        }
        newFilm.setGenres(new TreeSet<>(newFilm.getGenres()));
        setGenreName(newFilm);
        setMpaName(newFilm);
        deleteOldFilmGenres(newFilm);
        insertFilmGenres(newFilm);
        return getFilmById(newFilm.getId());
    }

    @Override
    public Film getFilmById(Long id) {
        String query = "SELECT * FROM films WHERE film_id = ?";
        try {
            Film film = jdbc.queryForObject(query, mapper, id);
            if (film == null) {
                throw new EmptyResultDataAccessException(1);
            }
            setGenres(film);
            setGenreName(film);
            setMpaName(film);
            setLikes(film);
            return film;
        } catch (EmptyResultDataAccessException ignored) {
            throw new NotFoundException("Фильм с id = " + id + " не найден");
        }
    }

    @Override
    public boolean isFilmExist(Long id) {
        try {
            getFilmById(id);
            return true;
        } catch (NotFoundException exp) {
            return false;
        }
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        String query = "INSERT INTO film_like(film_id, user_id)" +
                " VALUES (?, ?)";
        jdbc.update(query, filmId, userId);
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        String query = "DELETE FROM film_like" +
                " WHERE film_id = ? AND user_id = ?";
        jdbc.update(query, filmId, userId);
    }

    private void setGenres(Film film) {
        String query = "SELECT fg.genre_id\n" +
                " FROM films AS f\n" +
                " JOIN film_genre AS fg ON f.film_id = fg.film_id\n" +
                " WHERE f.film_id = ?";
        Set<Genre> genres = jdbc.queryForList(query, Long.class, film.getId())
                .stream()
                .map(id -> {
                    Genre genre = new Genre();
                    genre.setId(id);
                    return genre;
                }).collect(Collectors.toCollection(TreeSet::new));
        film.setGenres(genres);
    }

    private void insertFilmGenres(Film film) {
        for (Genre genre : film.getGenres()) {
            String query = "INSERT INTO film_genre(film_id, genre_id)" +
                    " VALUES (?, ?)";
            jdbc.update(query, film.getId(), genre.getId());
        }
    }

    private void deleteOldFilmGenres(Film film) {
        String query = "DELETE FROM film_genre" +
                " WHERE film_id = ?";
        jdbc.update(query, film.getId());
    }

    private void setLikes(Film film) {
        String query = "SELECT fl.user_id\n" +
                " FROM films AS f\n" +
                " JOIN film_like AS fl ON f.film_id = fl.film_id\n" +
                " WHERE f.film_id = ?";
        Set<Long> likes = new HashSet<>(jdbc.queryForList(query, Long.class, film.getId()));
        film.setLikes(likes);
    }

    private void setGenreName(Film film) {
        for (Genre genre : film.getGenres()) {
            String query = "SELECT name FROM genre WHERE genre_id = ?";
            genre.setName(jdbc.queryForObject(query, String.class, genre.getId()));
        }
    }

    private void setMpaName(Film film) {
        String query = "SELECT name FROM rating WHERE rating_id = ?";
        film.getMpa().setName(jdbc.queryForObject(query, String.class, film.getMpa().getId()));
    }
}
