package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.*;
import lombok.Data;
import ru.yandex.practicum.filmorate.group.AdvanceInfo;
import ru.yandex.practicum.filmorate.group.BaseInfo;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

@Data
public class Film {
    @NotNull(groups = AdvanceInfo.class)
    @Positive(groups = AdvanceInfo.class)
    private Long id;
    @NotBlank(groups = {BaseInfo.class, AdvanceInfo.class})
    private String name;
    @Size(max = 200, groups = {BaseInfo.class, AdvanceInfo.class})
    private String description;
    @PastOrPresent(groups = {BaseInfo.class, AdvanceInfo.class})
    private LocalDate releaseDate;
    @Positive(groups = {BaseInfo.class, AdvanceInfo.class})
    private Long duration;
    @JsonIgnore
    private Set<Long> likes = new HashSet<>();
    private Set<Genre> genres = new TreeSet<>();
    private Mpa mpa = new Mpa();
}
