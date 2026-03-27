package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.db.DbUserStorage;

import java.util.List;

@Service
public class UserService {
    private final DbUserStorage userStorage;

    @Autowired
    public UserService(DbUserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public List<User> getUsers() {
        return userStorage.getAllUser();
    }

    public User getUserById(Long id) {
        if (!userStorage.existsById(id)) {
            throw new NotFoundException("Пользователь с id=" + id + " не найден");
        }
        return userStorage.getUserById(id);
    }

    public User create(User user) {
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        return userStorage.createUser(user);
    }

    public User update(User newUser) {
        if (newUser.getName() == null) {
            newUser.setName(newUser.getLogin());
        }
        return userStorage.updateUser(newUser);
    }

    public void delete(Long id) {
        userStorage.deleteUser(id);
    }

    public void deleteFriend(Long userId, Long friendId) {
        if (!userStorage.existsById(userId)) {
            throw new NotFoundException("Пользователь с id=" + userId + " не найден");
        }

        //Проверяем, что друг существует
        if (!userStorage.existsById(friendId)) {
            throw new NotFoundException("Пользователь с id=" + friendId + " не найден");
        }
        userStorage.deleteFriends(userId, friendId);
    }

    public void addFriend(Long userId, Long friendId) {
        if (!userStorage.existsById(userId)) {
            throw new NotFoundException("Пользователь с id=" + userId + " не найден");
        }

        //Проверяем, что друг существует
        if (!userStorage.existsById(friendId)) {
            throw new NotFoundException("Пользователь с id=" + friendId + " не найден");
        }

        if (userId != friendId) {
            userStorage.addFriends(userId, friendId);
        } else {
            throw new ValidationException("Один и тотже пользователь не может подружиться сам с собой");
        }
    }

    public List<User> getCommonFriends(Long userId, Long otherUserId) {
        if (userId == otherUserId) {
            throw new ValidationException("Нельзя найти общих друзей с самим собой");
        }
        if (!userStorage.existsById(userId)) {
            throw new NotFoundException("Пользователь с id=" + userId + " не найден");
        }

        //Проверяем, что друг существует
        if (!userStorage.existsById(otherUserId)) {
            throw new NotFoundException("Пользователь с id=" + otherUserId + " не найден");
        }

        return userStorage.getCommonFriends(userId, otherUserId);
    }

    public List<User> getUserFriends(Long userId) {
        if (!userStorage.existsById(userId)) {
            throw new NotFoundException("Пользователь с id=" + userId + " не найден");
        }
        return userStorage.getUsersFriends(userId);
    }
}
