package ru.yandex.practicum.filmorate.storage.database;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.database.mappers.RatingRowMapper;

import java.util.Collection;

@Repository
@RequiredArgsConstructor
public class DbRatingStorage {
    private final JdbcTemplate jdbc;
    private final RatingRowMapper mapper;

    public Collection<Mpa> getAll() {
        String query = "SELECT * FROM rating";
        return jdbc.query(query, mapper);
    }

    public Mpa getRatingById(Long id) {
        String query = "SELECT * FROM rating WHERE rating_id = ?";
        try {
            return jdbc.queryForObject(query, mapper, id);
        } catch (EmptyResultDataAccessException ignored) {
            throw new NotFoundException("Возрастной рейтинг с id = " + id + " не найден");
        }
    }

    public boolean isRatingExist(Long id) {
        String query = "SELECT EXISTS (SELECT 1 FROM rating WHERE rating_id = ?)";
        Boolean isExist = jdbc.queryForObject(query, Boolean.class, id);
        if (isExist == null) {
            return false;
        }
        return isExist;
    }
}
