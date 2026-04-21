package ru.yandex.practicum.filmorate.storage.database;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.database.mappers.UserRowMapper;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
@RequiredArgsConstructor
@Qualifier("dbUserStorage")
public class DbUserStorage implements UserStorage {
    private final JdbcTemplate jdbc;
    private final UserRowMapper mapper;

    @Override
    public Collection<User> getAll() {
        String query = "SELECT * FROM users";
        return jdbc.query(query, mapper);
    }

    @Override
    public User create(User user) {
        String query = "INSERT INTO users(email, login, name, birthday)" +
                " VALUES (?, ?, ?, ?)";
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            ps.setObject(1, user.getEmail());
            ps.setObject(2, user.getLogin());
            ps.setObject(3, user.getName());
            ps.setObject(4, user.getBirthday());
            return ps;
        }, keyHolder);
        Long id = keyHolder.getKeyAs(Long.class);
        if (id != null) {
            user.setId(id);
            return user;
        } else {
            throw new InternalServerException("Не удалось создать нового пользователя");
        }
    }

    @Override
    public User update(User newUser) {
        String query = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE user_id = ?";
        int rowsUpdated = jdbc.update(query,
                newUser.getEmail(),
                newUser.getLogin(),
                newUser.getName(),
                newUser.getBirthday(),
                newUser.getId()
        );
        if (rowsUpdated == 0) {
            throw new InternalServerException("Не удалось обновить данные пользователя");
        }
        return getUserById(newUser.getId());
    }

    @Override
    public User getUserById(Long id) {
        String query = "SELECT * FROM users WHERE user_id = ?";
        try {
            User user = jdbc.queryForObject(query, mapper, id);
            if (user == null) {
                throw new EmptyResultDataAccessException(1);
            }
            return user;
        } catch (EmptyResultDataAccessException ignored) {
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }
    }

    @Override
    public List<User> getFriendsById(Long id) {
        String query = "SELECT u.user_id, u.email, u.login, u.name, u.birthday\n" +
                " FROM users AS u\n" +
                " JOIN friendship AS f ON u.user_id = f.friend_id\n" +
                " WHERE f.user_id = ? AND f.is_accept = TRUE";
        return jdbc.query(query, mapper, id);
    }

    @Override
    public boolean isUserExist(Long id) {
        try {
            getUserById(id);
            return true;
        } catch (NotFoundException exp) {
            return false;
        }
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        String query = "INSERT INTO friendship(user_id, friend_id, is_accept)" +
                " VALUES (?, ?, ?)";
        jdbc.update(query, userId, friendId, true);
    }

    @Override
    public void removeFriend(Long userId, Long friendId) {
        String query = "DELETE FROM friendship" +
                " WHERE user_id = ? AND friend_id = ? AND is_accept = TRUE";
        int rowsDeleted = jdbc.update(query, userId, friendId);
        if (rowsDeleted == 0) {
            throw new InternalServerException("Не удалось удалить из друзей у пользователя " +
                    "с userId = " + userId + " пользователя с friendId = " + friendId + ".");
        }
    }
}


