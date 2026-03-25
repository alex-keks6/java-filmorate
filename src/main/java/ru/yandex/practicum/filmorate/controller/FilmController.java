package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final Map<Long, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> getAll() {
        return films.values();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        // валидация
        log.info("Создание нового фильма {}", film);
        log.trace("Начало валидации создания нового фильма");
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            String errorMessage = "Некорректная дата релиза фильма: " + film.getReleaseDate() +
                    ". Релиз не может быть раньше дня рождения кино (28.12.1895)";
            log.debug(errorMessage);
            throw new ValidationException(errorMessage);
        }
        log.trace("Валидация создания нового фильма успешно пройдена");
        // создание
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Создан новый фильм с id = " + film.getId());

        return film;
    }

    private Long getNextId() {
        long maxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++maxId;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film newFilm) {
        // валидация
        log.info("Обновление данных о фильме данными {}", newFilm);
        log.trace("Начало валидации данных для обновления фильма");
        if (newFilm.getId() == null) {
            String errorMessage = "id фильма должен быть указан для обновления данных";
            log.debug(errorMessage);
            throw new ValidationException(errorMessage);
        }
        if (!films.containsKey(newFilm.getId())) {
            String errorMessage = "Фильм с id = " + newFilm.getId() + " для обновления не найден";
            log.debug(errorMessage);
            throw new NotFoundException(errorMessage);
        }
        if (newFilm.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            String errorMessage = "Некорректная дата релиза фильма: " + newFilm.getReleaseDate() +
                    ". Релиз не может быть раньше дня рождения кино (28.12.1895)";
            log.debug(errorMessage);
            throw new ValidationException(errorMessage);
        }
        log.trace("Валидация для обновления данных о фильме успешно пройдена");

        // обновление данных
        Film oldFilm = films.get(newFilm.getId());
        oldFilm.setName(newFilm.getName());
        oldFilm.setDescription(newFilm.getDescription());
        oldFilm.setReleaseDate(newFilm.getReleaseDate());
        oldFilm.setDuration(newFilm.getDuration());
        log.info("Обновлены данные о фильме с id = " + oldFilm.getId());

        return oldFilm;
    }
}
