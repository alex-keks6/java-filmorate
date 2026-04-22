package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.Collection;

@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor
@Slf4j
public class GenreController {
    private final GenreService genreService;

    @GetMapping
    public Collection<Genre> getAll() {
        return genreService.getAll();
    }

    @GetMapping("/{id}")
    public Genre get(@PathVariable Long id) {
        genreExist(id);
        return genreService.getGenreById(id);
    }

    private void genreExist(Long id) {
        if (!genreService.isGenreExist(id)) {
            String errorMessage = "Жанр с id = " + id + " не найден";
            log.debug(errorMessage);
            throw new NotFoundException(errorMessage);
        }
    }
}
