package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.RatingService;

import java.util.Collection;

@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
@Slf4j
public class RatingController {
    private final RatingService ratingService;

    @GetMapping
    public Collection<Mpa> getAll() {
        return ratingService.getAll();
    }

    @GetMapping("/{id}")
    public Mpa get(@PathVariable Long id) {
        ratingExist(id);
        return ratingService.getRatingById(id);
    }

    private void ratingExist(Long id) {
        if (!ratingService.isRatingExist(id)) {
            String errorMessage = "Возрастной рейтинг с id = " + id + " не найден";
            log.debug(errorMessage);
            throw new NotFoundException(errorMessage);
        }
    }
}
