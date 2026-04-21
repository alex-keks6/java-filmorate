package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.*;
import lombok.Data;
import ru.yandex.practicum.filmorate.group.AdvanceInfo;
import ru.yandex.practicum.filmorate.group.BaseInfo;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class User {
    @NotNull(groups = AdvanceInfo.class)
    @Positive(groups = AdvanceInfo.class)
    private Long id;
    @NotNull(groups = BaseInfo.class)
    @Email(groups = {BaseInfo.class, AdvanceInfo.class})
    private String email;
    @NotBlank(groups = {BaseInfo.class, AdvanceInfo.class})
    private String login;
    private String name;
    @PastOrPresent(groups = {BaseInfo.class, AdvanceInfo.class})
    private LocalDate birthday;
}
