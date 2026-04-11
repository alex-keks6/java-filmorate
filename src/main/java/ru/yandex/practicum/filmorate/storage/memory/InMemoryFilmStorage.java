package ru.yandex.practicum.filmorate.storage.memory;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();

    public Collection<Film> getAll() {
        return films.values();
    }

    public Film create(Film film) {
        film.setId(getNextId());
        films.put(film.getId(), film);
        return film;
    }

    public Film update(Film newFilm) {
        Film oldFilm = films.get(newFilm.getId());
        oldFilm.setName(newFilm.getName());
        oldFilm.setDescription(newFilm.getDescription());
        oldFilm.setReleaseDate(newFilm.getReleaseDate());
        oldFilm.setDuration(newFilm.getDuration());
        return oldFilm;
    }

    public Film getFilmById(Long id) {
        return films.get(id);
    }

    public boolean isFilmExist(Long id) {
        return films.containsKey(id);
    }

    private Long getNextId() {
        long maxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++maxId;
    }
}
