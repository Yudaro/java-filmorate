package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.groups.Default;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.validation.CreateGroup;
import ru.yandex.practicum.filmorate.validation.UpdateGroup;

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
        return userService.getUsers();
    }

    @GetMapping("/{userId}")
    public User getUserById(@PathVariable Long userId) {
        return userService.getUserById(userId);
    }

    // Создает нового пользователя
    @PostMapping
    public User postUser(@Validated({CreateGroup.class, Default.class}) @RequestBody User user) {
        return userService.create(user);
    }

    // Обновляет уже существующего пользователя
    @PutMapping
    public User putUser(@Validated({UpdateGroup.class, Default.class}) @RequestBody User newUser) {
        return userService.update(newUser);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.delete(id);
    }

    //Добавляет пользователя в друзья
    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable("id") Long userId, @PathVariable Long friendId) {
        userService.addFriend(userId, friendId);
    }

    // Возвращаем список друзей пользователя
    @GetMapping("/{id}/friends")
    public List<User> getFriendsUser(@PathVariable Long id) {
        return userService.getUserFriends(id);
    }

    //Удаляем пользователя из  друзей
    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriends(@PathVariable("id") Long id, @PathVariable Long friendId) {
        userService.deleteFriend(id, friendId);
    }

    // Возвращаем список общих друзей пользователей
    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getMutualFriends(@PathVariable("id") Long userId, @PathVariable Long otherId) {
        return userService.getCommonFriends(userId, otherId);
    }
}