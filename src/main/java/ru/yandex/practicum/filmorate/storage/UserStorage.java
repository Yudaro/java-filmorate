package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

//Интерфейс для работы с пользователями (добавление, удаление, модификация объектов)
public interface UserStorage {

    User createUser(User user);

    void deleteUser(Long id);

    User updateUser(User newUser);

    Collection<User> getAllUser();

    User getUserById(Long userId);
}