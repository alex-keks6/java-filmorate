package ru.yandex.practicum.filmorate.storage.memory;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();

    public Collection<Film> getAll() {
        return films.values();
    }

    public Film create(Film film) {
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Создан новый фильм с id = {}", film.getId());
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

    public Film update(Film newFilm) {
        if (!films.containsKey(newFilm.getId())) {
            String errorMessage = "Фильм с id = " + newFilm.getId() + " для обновления не найден";
            log.debug(errorMessage);
            throw new NotFoundException(errorMessage);
        }
        log.trace("Валидация для обновления данных о фильме успешно пройдена");

        Film oldFilm = films.get(newFilm.getId());
        oldFilm.setName(newFilm.getName());
        oldFilm.setDescription(newFilm.getDescription());
        oldFilm.setReleaseDate(newFilm.getReleaseDate());
        oldFilm.setDuration(newFilm.getDuration());
        log.info("Обновлены данные о фильме с id = " + oldFilm.getId());
        return oldFilm;
    }

    public Film getFilmById(Long id) {
        if (!films.containsKey(id)) {
            String errorMessage = "Фильм с id = " + id + " не найден";
            log.debug(errorMessage);
            throw new NotFoundException(errorMessage);
        }
        return films.get(id);
    }
}
