package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
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
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("Поле name у добавляемого пользователя пустое. Присвоено значение поля login");
        }
        return userStorage.create(user);
    }

    public User update(User newUser) {
        User oldUser = userStorage.getUserById(newUser.getId());
        if (newUser.getEmail() != null) {
            oldUser.setEmail(newUser.getEmail());
        }
        if (newUser.getName().isBlank()) {
            oldUser.setName(newUser.getLogin());
        } else {
            oldUser.setName(newUser.getName());
        }
        return userStorage.update(newUser);
    }

    public User addFriend(Long userId, Long friendId) {
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);
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

    public boolean isUserExist(Long id) {
        return userStorage.isUserExist(id);
    }
}
