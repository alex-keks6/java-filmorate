package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.group.AdvanceInfo;
import ru.yandex.practicum.filmorate.group.BaseInfo;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @GetMapping
    public Collection<User> getAll() {
        return userService.getAll();
    }

    @PostMapping
    public User create(@Validated(BaseInfo.class) @RequestBody User user) {
        log.trace("Начало валидации данных для создания нового пользователя");
        validate(user);
        log.trace("Валидация данных для создания нового пользователя успешно пройдена");
        return userService.create(user);
    }

    @PutMapping
    public User update(@Validated(AdvanceInfo.class) @RequestBody User newUser) {
        log.info("Обновление данных о пользователе данными {}", newUser);
        log.trace("Начало валидации данных для обновления пользователя");
        userExist(newUser.getId());
        validate(newUser);
        log.trace("Валидация данных для обновления пользователя успешно пройдена");
        return userService.update(newUser);
    }

    @GetMapping("/{id}")
    public User get(@PathVariable Long id) {
        userExist(id);
        return userService.getUserById(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable Long id, @PathVariable Long friendId) {
        userExist(id);
        userExist(friendId);
        if (isUsersBeFriends(id, friendId)) {
            String errorMessage = "У пользователя с id = " + id
                    + " в друзьях уже имеется пользователь с id = " + friendId + ".";
            log.debug(errorMessage);
            throw new ValidationException(errorMessage);
        }
        if (userService.isFriendRequestExist(friendId, id)) {
            String errorMessage = "У пользователя с id = " + friendId
                    + " уже имеется заявка в друзья от пользователя с id = " + id + ".";
            log.debug(errorMessage);
            throw new ValidationException(errorMessage);
        }
        userService.addFriend(id, friendId);
    }

    @PutMapping("/{id}/friends/accept/{friendId}")
    public void acceptFriend(@PathVariable Long id, @PathVariable Long friendId) {
        userExist(id);
        userExist(friendId);
        if (!userService.isFriendRequestExist(id, friendId)) {
            String errorMessage = "У пользователя с id = " + id
                    + " нет заявки в друзья от пользователя с id = " + friendId + ".";
            log.debug(errorMessage);
            throw new ValidationException(errorMessage);
        }
        userService.acceptFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable Long id, @PathVariable Long friendId) {
        userExist(id);
        userExist(friendId);
        if (!isUsersBeFriends(id, friendId)) {
            String errorMessage = "У пользователя с id = " + id
                    + " в друзьях нет пользователя с id = " + friendId + ".";
            log.debug(errorMessage);
            throw new ValidationException(errorMessage);
        }
        userService.removeFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriendsById(@PathVariable Long id) {
        userExist(id);
        return userService.getFriendsById(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable Long id, @PathVariable Long otherId) {
        userExist(id);
        userExist(otherId);
        return userService.getCommonFriends(id, otherId);
    }

    private void validate(User user) {
        if (user.getLogin().contains(" ")) {
            String errorMessage = "Логин не должен содержать пробелов";
            log.debug(errorMessage);
            throw new ValidationException(errorMessage);
        }
    }

    private void userExist(Long id) {
        if (!userService.isUserExist(id)) {
            String errorMessage = "Пользователь с id = " + id + " не найден";
            log.debug(errorMessage);
            throw new NotFoundException(errorMessage);
        }
    }

    private boolean isUsersBeFriends(Long userId, Long friendId) {
        return userService.getFriendsById(userId).contains(userService.getUserById(friendId));
    }
}
