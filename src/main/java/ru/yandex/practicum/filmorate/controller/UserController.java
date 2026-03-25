package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> getAll() {
        return users.values();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        // валидация
        log.info("Создание нового пользователя {}", user);
        log.trace("Начало валидации создания нового пользователя");
        if (user.getLogin().contains(" ")) {
            String errorMessage = "Логин не должен содержать пробелов";
            log.debug(errorMessage);
            throw new ValidationException(errorMessage);
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.trace("Поле name у добавляемого пользователя пустое. Присвоено значение поля login");
        }
        log.trace("Валидация создания нового пользователя успешно пройдена");

        // создание
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Создан новый пользователь с id = " + user.getId());

        return user;
    }

    private Long getNextId() {
        long maxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++maxId;
    }

    @PutMapping
    public User update(@Valid @RequestBody User newUser) {
        // валидация
        log.info("Обновление данных о пользователе данными {}", newUser);
        log.trace("Начало валидации данных для обновления пользователя");
        if (newUser.getId() == null) {
            String errorMessage = "id пользователя должен быть указан для обновления данных";
            log.debug(errorMessage);
            throw new ValidationException(errorMessage);
        }
        if (!users.containsKey(newUser.getId())) {
            String errorMessage = "Пользователь с id = " + newUser.getId() + " для обновления не найден";
            log.debug(errorMessage);
            throw new NotFoundException(errorMessage);
        }
        if (newUser.getLogin().contains(" ")) {
            String errorMessage = "Логин не должен содержать пробелов";
            log.debug(errorMessage);
            throw new ValidationException(errorMessage);
        }
        log.trace("Валидация для обновления данных о пользователе успешно пройдена");

        // обновление данных
        User oldUser = users.get(newUser.getId());
        oldUser.setName(newUser.getName());
        oldUser.setEmail(newUser.getEmail());
        oldUser.setLogin(newUser.getLogin());
        if (newUser.getName() == null || newUser.getName().isBlank()) {
            oldUser.setName(newUser.getLogin());
        } else {
            oldUser.setName(newUser.getName());

        }
        oldUser.setBirthday(newUser.getBirthday());
        log.info("Обновлены данные о пользователе с id = " + oldUser.getId());

        return oldUser;
    }
}
