package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final LocalDate movieBirthday = LocalDate.of(1895, 12, 28);

    // явный конструктор из-за final поля movieBirthday, которое не нужно инициализировать конструктором
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Collection<Film> getAll() {
        return filmStorage.getAll();
    }

    public Film create(Film film) {
        log.info("Создание нового фильма {}", film);
        log.trace("Начало валидации создания нового фильма");
        validate(film);
        log.trace("Валидация создания нового фильма успешно пройдена");
        return filmStorage.create(film);
    }

    public Film update(Film newFilm) {
        log.info("Обновление данных о фильме данными {}", newFilm);
        log.trace("Начало валидации данных для обновления фильма");
        validate(newFilm);
        return filmStorage.update(newFilm);
    }

    private void validate(Film film) {
        if (film.getReleaseDate().isBefore(movieBirthday)) {
            String errorMessage = "Некорректная дата релиза фильма: " + film.getReleaseDate() +
                    ". Релиз не может быть раньше дня рождения кино (28.12.1895)";
            log.debug(errorMessage);
            throw new ValidationException(errorMessage);
        }
    }

    public Film addLike(Long filmId, Long userId) {
        Film film = filmStorage.getFilmById(filmId);
        film.getLikes().add(userId);
        log.info("Добавлен лайк фильму с filmId = {} от пользователя с userId = {}", filmId, userId);
        return film;
    }

    public Film removeLike(Long filmId, Long userId) {
        Film film = filmStorage.getFilmById(filmId);
        film.getLikes().remove(userId);
        log.info("Удален лайк фильму с filmId = {} пользователем с userId = {}", filmId, userId);
        return film;
    }

    public List<Film> getPopularFilms(int count) {
        log.info("Получение самых популярных фильмов в размере count = {}", count);
        return filmStorage.getAll().stream()
                .sorted((film1, film2) -> film2.getLikes().size() - film1.getLikes().size())
                .limit(count)
                .toList();
    }

    public Film getFilmById(Long id) {
        return filmStorage.getFilmById(id);
    }
}
