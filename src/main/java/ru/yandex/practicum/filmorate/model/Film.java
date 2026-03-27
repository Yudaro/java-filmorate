package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;
import ru.yandex.practicum.filmorate.validation.CreateGroup;
import ru.yandex.practicum.filmorate.validation.UpdateGroup;

import java.time.LocalDate;
import java.util.*;

@Data
public class Film {
    @Null(message = "Поле id - должно быть пустым при создании.", groups = CreateGroup.class)
    @NotNull(message = "Поле id - не должно быть пустым при обновлении.", groups = UpdateGroup.class)
    private Long id;
    @NotBlank(message = "name(название фильма) - не может быть пустым.")
    private String name;
    @NotBlank(message = "description(описание) - не может быть пустым.", groups = UpdateGroup.class)
    @Size(max = 200, message = "description(описание фильма) - не должно содержать больше 200 символов.")
    private String description;
    @NotNull(message = "releaseDate(дата выхода) - не может быть пустым.", groups = UpdateGroup.class)
    private LocalDate releaseDate;
    @NotNull(message = "duration(продолжительность фильма) - не может быть пустым.", groups = UpdateGroup.class)
    @Positive(message = "duration(продолжительность фильма в минутых) - не может быть отрицательным или равна 0.")
    private Long duration;
    private Mpa mpa;
    private Set<Long> likes = new HashSet<>();
    private Set<Genre> genres = new TreeSet<>(Comparator.comparingLong(Genre::getId));
    @Null(message = "Поле popular - должно быть пустым при создании.", groups = CreateGroup.class)
    private Long popular;
}