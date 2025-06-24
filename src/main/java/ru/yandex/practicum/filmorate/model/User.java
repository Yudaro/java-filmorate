package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;
import ru.yandex.practicum.filmorate.validation.CreateGroup;
import ru.yandex.practicum.filmorate.validation.UpdateGroup;

import java.time.LocalDate;

@Data
public class User {
    @NotBlank(message = "Поле id - не должно быть пустым при обновлении.", groups = UpdateGroup.class)
    @Null(message = "Поле id - должно быть пустым при создании.", groups = CreateGroup.class)
    private Long id;
    @NotBlank(message = "Поле email - не должно быть пустым.")
    @Email(message = "Некорректный email.")
    private String email;
    @NotEmpty(message = "login - не может быть пустым.")
    private String login;
    private String name;
    @Past(message = "Дата рождения не может быть в будущем.")
    private LocalDate birthday;
}
