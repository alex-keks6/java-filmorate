package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserStorage userStorage;

    public Collection<User> getAll() {
        return userStorage.getAll();
    }

    public User create(User user) {
        log.info("Создание нового пользователя {}", user);
        log.trace("Начало валидации создания нового пользователя");
        validate(user);
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("Поле name у добавляемого пользователя пустое. Присвоено значение поля login");
        }
        log.trace("Валидация создания нового пользователя успешно пройдена");
        return userStorage.create(user);
    }

    public User update(User newUser) {
        log.info("Обновление данных о пользователе данными {}", newUser);
        log.trace("Начало валидации данных для обновления пользователя");
        validate(newUser);
        return userStorage.update(newUser);
    }

    private void validate(User user) {
        if (user.getLogin().contains(" ")) {
            String errorMessage = "Логин не должен содержать пробелов";
            log.debug(errorMessage);
            throw new ValidationException(errorMessage);
        }
    }

    public User addFriend(Long userId, Long friendId) {
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);
        log.info("Пользователь с userId = {} добавил в друзья пользователя с friendId = {}", userId, friendId);
        if (user.getFriends().add(friendId)) {
            log.info("Пользователь с userId = {} добавил в друзья пользователя с friendId = {}", userId, friendId);
        } else {
            log.debug("Добавление у пользователя с userId = {} друга с friendId = {} не произошло", userId, friendId);
        }
        if (friend.getFriends().add(userId)) {
            log.info("Пользователь с userId = {} добавил в друзья пользователя с friendId = {}", friendId, userId);
        } else {
            log.debug("Добавление у пользователя с userId = {} друга с friendId = {} не произошло", friendId, userId);
        }
        return user;
    }

    public void removeFriend(Long userId, Long friendId) {
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);
        if (user.getFriends().remove(friendId)) {
            log.info("Пользователь с userId = {} удалил из друзей пользователя с friendId = {}", userId, friendId);
        } else {
            log.debug("Удаление у пользователя с userId = {} друга с friendId = {} не произошло", userId, friendId);
        }
        if (friend.getFriends().remove(userId)) {
            log.info("Пользователь с userId = {} удалил из друзей пользователя с friendId = {}", friendId, userId);
        } else {
            log.debug("Удаление у пользователя с userId = {} друга с friendId = {} не произошло", friendId, userId);
        }
    }

    public List<User> getFriendsById(Long id) {
        return userStorage.getFriendsById(id);
    }

    public List<User> getCommonFriends(Long userId, Long otherId) {
        log.info("Получение общих друзей пользователя с userId = {} и пользователя с otherId = {}", userId, otherId);
        Set<Long> userFriends = userStorage.getUserById(userId).getFriends();
        Set<Long> friendFriends = userStorage.getUserById(otherId).getFriends();
        return userFriends.stream()
                .filter(friendFriends::contains)
                .map(userStorage::getUserById)
                .collect(Collectors.toList());
    }

    public User getUserById(Long id) {
        return userStorage.getUserById(id);
    }
}
