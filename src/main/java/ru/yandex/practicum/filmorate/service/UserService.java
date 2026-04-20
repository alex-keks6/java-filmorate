package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;

    public UserService(@Qualifier("dbUserStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

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
        if (newUser.getEmail() == null) {
            newUser.setEmail(oldUser.getEmail());
        }
        if (newUser.getName().isBlank()) {
            newUser.setName(newUser.getLogin());
        }
        return userStorage.update(newUser);
    }

    public void addFriend(Long userId, Long friendId) {
        userStorage.addFriend(userId, friendId);
        log.info("Пользователь с userId = {} добавил в друзья пользователя с friendId = {}",
                userId, friendId);
    }

    public void removeFriend(Long userId, Long friendId) {
        userStorage.removeFriend(userId, friendId);
        log.info("Пользователь с userId = {} удалил из друзей пользователя с friendId = {}", userId, friendId);
    }

    public List<User> getFriendsById(Long id) {
        return userStorage.getFriendsById(id);
    }

    public List<User> getCommonFriends(Long userId, Long otherId) {
        log.info("Получение общих друзей пользователя с userId = {} и пользователя с otherId = {}", userId, otherId);
        Set<Long> userFriends = userStorage.getUserById(userId).getFriends();
        Set<Long> otherFriends = userStorage.getUserById(otherId).getFriends();
        return userFriends.stream()
                .filter(otherFriends::contains)
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
