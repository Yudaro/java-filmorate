package ru.yandex.practicum.filmorate.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Override
    public User create(User user) {
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

    @Override
    public User delete(User user) {
        return null;
    }

    @Override
    public User update(User newUser) {
        log.info("Начало выполнения метода putUser(Обновление пользователя)");

        if (!users.containsKey(newUser.getId())) {
            log.warn("В программе нет пользователя с таким id - " + newUser.getId());
            throw new NotFoundException("Некорректный id пользователя - " + newUser.getId());
        }
        User oldUser = users.get(newUser.getId());
        log.info("Пользователь которого необходимо обновить найден");

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

    @Override
    public User getUserById(Long userId) {
        log.info("Начало выполнения метода поиска пользователя по id.");
        if (!users.containsKey(userId)) {
            log.warn("В программе нет пользователя с таким id - " + userId);
            throw new NotFoundException("Некорректный id пользователя - " + userId);
        }
        log.info("Пользователь найден.");
        return users.get(userId);
    }

    @Override
    public Collection<User> getMutualFriends(Long userId, Long otherId) {
        User user = getUserById(userId);
        User otherUser = getUserById(otherId);

        return user.getFriends().stream()
                .filter(otherUser.getFriends()::contains)
                .map(this::getUserById)
                .toList();
    }

    @Override
    public long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    @Override
    public Collection<User> findAll() {
        log.info("Начало выполнения метода getUser(Возвращаем всех пользователей)");
        return users.values();
    }
}
