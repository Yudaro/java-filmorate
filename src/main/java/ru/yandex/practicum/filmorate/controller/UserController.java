package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;

@Validated
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Возвращает всех пользователей
    @GetMapping
    public Collection<User> getUsers() {
        return userService.findAll();
    }

    // Создает нового пользователя
    @PostMapping
    public User postUser(@Valid @RequestBody User user) {
        return userService.create(user);
    }

    // Обновляет уже существующего пользователя
    @PutMapping
    public User putUser(@Valid @RequestBody User newUser) {
        return userService.update(newUser);
    }
}