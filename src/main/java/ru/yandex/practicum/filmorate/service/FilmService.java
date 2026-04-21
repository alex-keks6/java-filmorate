package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;
import java.util.List;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;

    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Collection<Film> getAll() {
        return filmStorage.getAll();
    }

    public Film create(Film film) {
        log.info("Создание нового фильма {}", film);
        return filmStorage.create(film);
    }

    public Film update(Film newFilm) {
        log.info("Обновление данных о фильме данными {}", newFilm);
        return filmStorage.update(newFilm);
    }

    public void addLike(Long filmId, Long userId) {
        filmStorage.addLike(filmId, userId);
        log.info("Добавлен лайк фильму с filmId = {} от пользователя с userId = {}", filmId, userId);
    }

    public void removeLike(Long filmId, Long userId) {
        filmStorage.removeLike(filmId, userId);
        log.info("Удален лайк фильму с filmId = {} пользователем с userId = {}", filmId, userId);
    }

    public List<Film> getPopularFilms(int count) {
        log.info("Получение самых популярных фильмов в размере count = {}", count);
        return filmStorage.getPopularFilms(count);
    }

    public Film getFilmById(Long id) {
        return filmStorage.getFilmById(id);
    }

    public boolean isFilmExist(Long id) {
        return filmStorage.isFilmExist(id);
    }
}
