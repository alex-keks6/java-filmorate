package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class Mpa {
    private Long id;
    private String name;

    public Mpa() {
    }

    public Mpa(Long id) {
        this.id = id;
    }
}
