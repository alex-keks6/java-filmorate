package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.group.AdvanceInfo;
import ru.yandex.practicum.filmorate.group.BaseInfo;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
@Slf4j
public class FilmController {
    private final FilmService filmService;
    private final UserService userService;
    private static final LocalDate MOVIE_BIRTHDAY = LocalDate.of(1895, 12, 28);

    @GetMapping
    public Collection<Film> getAll() {
        return filmService.getAll();
    }

    @PostMapping
    public Film create(@Validated(BaseInfo.class) @RequestBody Film film) {
        log.trace("Начало валидации данных для создания нового фильма");
        validate(film);
        log.trace("Валидация данных для создания нового фильма успешно пройдена");
        return filmService.create(film);
    }

    @PutMapping
    public Film update(@Validated(AdvanceInfo.class) @RequestBody Film newFilm) {
        log.trace("Начало валидации данных для обновления фильма");
        filmExist(newFilm.getId());
        validate(newFilm);
        log.trace("Валидация данных для обновления фильма успешно пройдена");
        return filmService.update(newFilm);
    }

    @GetMapping("/{id}")
    public Film get(@PathVariable Long id) {
        filmExist(id);
        return filmService.getFilmById(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Long id, @PathVariable Long userId) {
        filmExist(id);
        userExist(userId);
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable Long id, @PathVariable Long userId) {
        filmExist(id);
        userExist(userId);
        filmService.removeLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(defaultValue = "10") int count) {
        return filmService.getPopularFilms(count);
    }

    private void validate(Film film) {
        if (film.getReleaseDate().isBefore(MOVIE_BIRTHDAY)) {
            String errorMessage = "Некорректная дата релиза фильма: " + film.getReleaseDate() +
                    ". Релиз не может быть раньше дня рождения кино (28.12.1895)";
            log.debug(errorMessage);
            throw new ValidationException(errorMessage);
        }
        if (film.getMpa().getId() != null && (film.getMpa().getId() > 5 || film.getMpa().getId() < 1)) {
            String errorMessage = "Некорректный идентификатор возрастного рейтинга фильма: " + film.getMpa().getId()
                    + ".";
            log.debug(errorMessage);
            throw new NotFoundException(errorMessage);
        }
        for (Genre genre : film.getGenres()) {
            if (genre.getId() > 6 || genre.getId() < 1) {
                String errorMessage = "Некорректный идентификатор жанра фильма: " + genre.getId() + ".";
                log.debug(errorMessage);
                throw new NotFoundException(errorMessage);
            }
        }
    }

    private void userExist(Long id) {
        if (!userService.isUserExist(id)) {
            String errorMessage = "Пользователь с id = " + id + " не найден";
            log.debug(errorMessage);
            throw new NotFoundException(errorMessage);
        }
    }

    private void filmExist(Long id) {
        if (!filmService.isFilmExist(id)) {
            String errorMessage = "Фильм с id = " + id + " не найден";
            log.debug(errorMessage);
            throw new NotFoundException(errorMessage);
        }
    }
}
