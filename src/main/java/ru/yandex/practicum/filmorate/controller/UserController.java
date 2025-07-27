package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;
import java.util.List;

@Validated
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Возвращает всех пользователей
    @GetMapping
    public Collection<User> getUsers() {
        return userService.findAll();
    }

    @GetMapping("/{userId}")
    public User getUserById(@PathVariable Long userId) {
        return userService.getUserById(userId);
    }

    // Создает нового пользователя
    @PostMapping
    public User postUser(@Valid @RequestBody User user) {
        return userService.createUser(user);
    }

    // Обновляет уже существующего пользователя
    @PutMapping
    public User putUser(@Valid @RequestBody User newUser) {
        return userService.updateUser(newUser);
    }

    // Добавляет пользователя в друзья
    @PutMapping("/{id}/friends/{friendId}")
    public Collection<User> addFriend(@PathVariable("id") Long userId, @PathVariable Long friendId) {
        return userService.addFriend(userId, friendId);
    }

    // Возвращаем список друзей пользователя
    @GetMapping("/{id}/friends")
    public List<User> getFriendsUser(@PathVariable Long id) {
        return userService.findAllFriends(id);
    }

    // Удаляем пользователя из друзей
    @DeleteMapping("/{id}/friends/{friendId}")
    public String deleteFriends(@PathVariable("id") Long userId, @PathVariable Long friendId) {
        return userService.deleteFriend(userId, friendId);
    }

    // Возвращаем список общих друзей пользователей
    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getMutualFriends(@PathVariable("id") Long userId, @PathVariable Long otherId) {
        return userService.getMutualFriends(userId, otherId);
    }
}