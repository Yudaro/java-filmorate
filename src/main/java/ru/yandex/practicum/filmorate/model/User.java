package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Null;
import lombok.Data;

import java.time.LocalDate;

@Data
public class User {
    @Null(message = "Поле id - должно быть пустым при создании.")
    private Long id;
    @Email(message = "Некорректный email.")
    private String email;
    @NotBlank(message = "login - не может быть пустым.")
    private String login;
    private String name;
    private LocalDate birthday;
}
