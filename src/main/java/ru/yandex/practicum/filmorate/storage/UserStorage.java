package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;

//Интерфейс для работы с пользователями (добавление, удаление, модификация объектов)
public interface UserStorage {

    User create(User user);

    User delete(User user);

    User update(User newUser);

    long getNextId();

    Collection<User> findAll();

    User getUserById(Long userId);

    List<User> getMutualFriends(Long userId, Long otherId);

    void checkUserById(Long id);
}
