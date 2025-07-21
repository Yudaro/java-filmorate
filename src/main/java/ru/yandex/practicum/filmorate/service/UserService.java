package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class UserService {
    public UserStorage userStorage;

    @Autowired
    public UserService(InMemoryUserStorage userStorage) {
        this.userStorage = userStorage;
    }

    //Тут мы возвращаем всех пользователей, которые являются друзьями переданного нам пользователя.
    // Перебираем весь список друзей и получаем по id объект User, после чего возвращаем List<User>
    public Collection<User> findAllFriends(Long id) {
        User user = userStorage.getUserById(id);

        List<User> users = new ArrayList<>();
        for (Long friendId : user.getFriends()) {
            users.add(userStorage.getUserById(friendId));
        }
        return users;
    }

    //Процесс добавления пользователя в друзья. Пытаемся получить пользователей, если один из пользователей отсутствует
    // будет выброшено исключение потом добавляем в друзья друг другу.
    public Collection<User> addFriend(Long userId, Long friendId) {
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);
        user.addFrend(friend.getId());
        friend.addFrend(user.getId());
        return findAllFriends(userId);
    }

    //Процесс добавления пользователя в друзья. Пытаемся получить пользователей, если один из пользователей отсутствует
    // будет выброшено исключение, удаляем из друзей друг у друга.
    public String deleteFriend(Long userId, Long friendId) {
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);
        user.deleteFriend(friendId);
        friend.deleteFriend(userId);
        return "Пользователи с id - " + userId + " и " + friendId + " перестали дружить.";
    }
}
