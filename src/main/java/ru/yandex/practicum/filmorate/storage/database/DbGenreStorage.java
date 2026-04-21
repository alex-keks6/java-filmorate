package ru.yandex.practicum.filmorate.storage.database;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.database.mappers.GenreRowMapper;

import java.util.Collection;

@Repository
@RequiredArgsConstructor
public class DbGenreStorage {
    private final JdbcTemplate jdbc;
    private final GenreRowMapper mapper;

    public Collection<Genre> getAll() {
        String query = "SELECT * FROM genre";
        return jdbc.query(query, mapper);
    }

    public Genre getGenreById(Long id) {
        String query = "SELECT * FROM genre WHERE genre_id = ?";
        try {
            return jdbc.queryForObject(query, mapper, id);
        } catch (EmptyResultDataAccessException ignored) {
            throw new NotFoundException("Жанр с id = " + id + " не найден");
        }
    }

    public boolean isGenreExist(Long id) {
        String query = "SELECT EXISTS (SELECT 1 FROM genre WHERE genre_id = ?)";
        Boolean isExist = jdbc.queryForObject(query, Boolean.class, id);
        if (isExist == null) {
            return false;
        }
        return isExist;
    }
}
