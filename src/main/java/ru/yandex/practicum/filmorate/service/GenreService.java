package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.database.DbGenreStorage;

import java.util.Collection;

@Service
@Slf4j
@RequiredArgsConstructor
public class GenreService {
    private final DbGenreStorage genreStorage;

    public Collection<Genre> getAll() {
        log.info("Получение списка всех жанров фильмов");
        return genreStorage.getAll();
    }

    public Genre getGenreById(Long id) {
        log.info("Получение жанра с id = {}", id);
        return genreStorage.getGenreById(id);
    }

    public boolean isGenreExist(Long id) {
        return genreStorage.isGenreExist(id);
    }
}
