package ru.yandex.practicum.filmorate.storage.memory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();

    public Collection<User> getAll() {
        return users.values();
    }

    public User create(User user) {
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Создан новый пользователь с id = {}", user.getId());
        return user;
    }

    private Long getNextId() {
        long maxId = users.keySet().stream().mapToLong(id -> id).max().orElse(0);
        return ++maxId;
    }

    public User update(User newUser) {
        if (!users.containsKey(newUser.getId())) {
            String errorMessage = "Пользователь с id = " + newUser.getId() + " для обновления не найден";
            log.debug(errorMessage);
            throw new NotFoundException(errorMessage);
        }
        log.trace("Валидация для обновления данных о пользователе успешно пройдена");

        User oldUser = users.get(newUser.getId());
        oldUser.setName(newUser.getName());
        // чтобы не записать в email null, так как при обновлении email необязателен
        if (newUser.getEmail() != null) {
            oldUser.setEmail(newUser.getEmail());
        }
        oldUser.setLogin(newUser.getLogin());
        if (newUser.getName().isBlank()) {
            oldUser.setName(newUser.getLogin());
        } else {
            oldUser.setName(newUser.getName());

        }
        oldUser.setBirthday(newUser.getBirthday());
        log.info("Обновлены данные о пользователе с id = {}", oldUser.getId());
        return oldUser;
    }

    public User getUserById(Long id) {
        validateExistUserById(id);
        return users.get(id);
    }

    public List<User> getFriendsById(Long id) {
        validateExistUserById(id);
        return users.get(id).getFriends().stream().map(this::getUserById).toList();
    }

    public void validateExistUserById(Long id) {
        if (!users.containsKey(id)) {
            String errorMessage = "Пользователь с id = " + id + " не найден";
            log.debug(errorMessage);
            throw new NotFoundException(errorMessage);
        }
    }
}
