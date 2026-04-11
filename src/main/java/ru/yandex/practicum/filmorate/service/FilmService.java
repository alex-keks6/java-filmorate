package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;

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

    public boolean isFilmExist(Long id) {
        return filmStorage.isFilmExist(id);
    }
}
