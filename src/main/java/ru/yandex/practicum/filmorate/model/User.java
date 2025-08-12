package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;
import ru.yandex.practicum.filmorate.enums.StatusFriendRequest;
import ru.yandex.practicum.filmorate.validation.CreateGroup;
import ru.yandex.practicum.filmorate.validation.UpdateGroup;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

@Data
public class User {
    @NotBlank(message = "Поле id - не должно быть пустым при обновлении.", groups = UpdateGroup.class)
    @Null(message = "Поле id - должно быть пустым при создании.", groups = CreateGroup.class)
    private Long id;
    @NotBlank(message = "Поле email - не должно быть пустым.")
    @Email(message = "Некорректный email.")
    private String email;
    @NotEmpty(message = "login - не может быть пустым.")
    private String login;
    private String name;
    @Past(message = "Дата рождения не может быть в будущем.")
    private LocalDate birthday;
    private Set<Long> friends = new TreeSet<>();
    private StatusFriendRequest status;

    public void addFrend(Long id) {
        friends.add(id);
    }

    public Set<Long> getFriends() {
        return Collections.unmodifiableSet(friends);
    }

    public void deleteFriend(Long friendId) {
        friends.remove(friendId);
    }
}
