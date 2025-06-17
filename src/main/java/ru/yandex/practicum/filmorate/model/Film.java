package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class Film {
    @Null(message = "Поле id - должно быть пустым при создании.")
    private Long id;
    @NotBlank(message = "name(название фильма) - не может быть пустым.")
    private String name;
    @Size(max = 200, message = "description(описание фильма) - не должно содержать больше 200 символов.")
    private String description;
    private LocalDate releaseDate;
    @Positive(message = "duration(продолжительность фильма в минутых) - не может быть отрицательным или равна 0.")
    private Long duration;
}
