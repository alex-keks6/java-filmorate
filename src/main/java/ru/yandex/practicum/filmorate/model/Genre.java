package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class Genre implements Comparable<Genre> {
    private Long id;
    private String name;

    public Genre() {
    }

    public Genre(Long id) {
        this.id = id;
    }

    @Override
    public int compareTo(Genre o) {
        return Long.compare(this.id, o.getId());
    }
}
