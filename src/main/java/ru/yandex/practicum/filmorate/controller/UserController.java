package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Validated
@RestController
@RequestMapping("/users")
public class UserController {
    private Map<Long, User> users = new HashMap<>();
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    // Возвращает всех пользователей
    @GetMapping
    public Collection<User> getUsers() {
        log.info("Начало выполнения метода getUser(Возвращаем всех пользователей)");
        return users.values();
    }

    // Создает нового пользователя
    @PostMapping
    public User postUser(@Valid @RequestBody User user) {

        log.info("Начало выполнения метода postUser(Создание пользователя)");
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Дата родления не может быть в будущем.");
            throw new ValidationException("Дата родления не может быть в будущем.");
        }

        if (user.getName() == null) {
            user.setName(user.getLogin());
        }

        log.info("Метод postUser отработал корректно, начался процесс добавления нового пользователя");
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Новый пользователь добавлен");
        return user;
    }

    // Обновляет уже существующего пользователя
    @PutMapping
    public User putUser(@RequestBody User newUser) {
        log.info("Начало выполнения метода putUser(Обновление пользователя)");
        if (newUser.getId() == null) {
            log.warn("Отсутствует id клиента которого необходимо изменить.");
            throw new ValidationException("id - должне быть указан.");
        }

        if (!users.containsKey(newUser.getId())) {
            log.warn("В программе нет пользователя с таким id - " + newUser.getId());
            throw new ValidationException("Некорректный id пользователя - " + newUser.getId());
        }
        User oldUser = users.get(newUser.getId());
        log.info("Пользоваетль которого необходимо обновить найден");

        if (newUser.getEmail() == null) {
            log.info("email - не будет изменен");
            newUser.setEmail(oldUser.getEmail());
        }

        if (newUser.getLogin() == null) {
            log.info("login - не будет изменен");
            newUser.setLogin(oldUser.getLogin());
        }

        if (newUser.getName() == null) {
            log.info("name - не будет изменен");
            newUser.setLogin(oldUser.getName());
        }

        if (newUser.getBirthday() == null) {
            log.info("birthday - не будет изменен");
            newUser.setBirthday(oldUser.getBirthday());
        }

        users.put(newUser.getId(), newUser);
        log.info("Пользователь обновлен");
        return newUser;
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
