package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.database.DbRatingStorage;

import java.util.Collection;

@Service
@Slf4j
@RequiredArgsConstructor
public class RatingService {
    private final DbRatingStorage ratingStorage;

    public Collection<Mpa> getAll() {
        return ratingStorage.getAll();
    }

    public Mpa getRatingById(Long id) {
        return ratingStorage.getRatingById(id);
    }

    public boolean isRatingExist(Long id) {
        return ratingStorage.isRatingExist(id);
    }
}
