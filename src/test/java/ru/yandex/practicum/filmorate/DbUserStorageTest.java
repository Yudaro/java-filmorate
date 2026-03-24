package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.db.DbUserStorage;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class DbUserStorageTest {

    @Autowired
    private DbUserStorage userStorage;

    @Autowired
    private JdbcTemplate jdbc;

    private User user;

    @BeforeEach
    void setUp() {
        jdbc.update("DELETE FROM likes");
        jdbc.update("DELETE FROM film_genre");
        jdbc.update("DELETE FROM friendship");
        jdbc.update("DELETE FROM films");
        jdbc.update("DELETE FROM users");

        user = new User();
        user.setEmail("user@mail.com");
        user.setLogin("userLogin");
        user.setName("User Name");
        user.setBirthday(LocalDate.of(2000, 1, 1));
    }

    @Test
    @DisplayName("createUser: должен сохранять пользователя в БД")
    void shouldCreateUser() {
        User createdUser = userStorage.createUser(user);

        assertNotNull(createdUser.getId());
        assertEquals("user@mail.com", createdUser.getEmail());
        assertEquals("userLogin", createdUser.getLogin());
        assertEquals("User Name", createdUser.getName());
        assertEquals(LocalDate.of(2000, 1, 1), createdUser.getBirthday());
    }

    @Test
    @DisplayName("getUserById: должен возвращать пользователя по id")
    void shouldReturnUserById() {
        User createdUser = userStorage.createUser(user);

        User foundUser = userStorage.getUserById(createdUser.getId());

        assertNotNull(foundUser);
        assertEquals(createdUser.getId(), foundUser.getId());
        assertEquals(createdUser.getEmail(), foundUser.getEmail());
        assertEquals(createdUser.getLogin(), foundUser.getLogin());
        assertEquals(createdUser.getName(), foundUser.getName());
        assertEquals(createdUser.getBirthday(), foundUser.getBirthday());
    }

    @Test
    @DisplayName("getUserById: должен выбрасывать NotFoundException для несуществующего пользователя")
    void shouldThrowExceptionWhenUserNotFound() {
        assertThrows(NotFoundException.class, () -> userStorage.getUserById(99999L));
    }

    @Test
    @DisplayName("getAllUser: должен возвращать список всех пользователей")
    void shouldReturnAllUsers() {
        User user1 = new User();
        user1.setEmail("user1@mail.com");
        user1.setLogin("login1");
        user1.setName("User 1");
        user1.setBirthday(LocalDate.of(1990, 1, 1));

        User user2 = new User();
        user2.setEmail("user2@mail.com");
        user2.setLogin("login2");
        user2.setName("User 2");
        user2.setBirthday(LocalDate.of(1992, 2, 2));

        userStorage.createUser(user1);
        userStorage.createUser(user2);

        List<User> users = userStorage.getAllUser();

        assertEquals(2, users.size());
        assertEquals("user1@mail.com", users.get(0).getEmail());
        assertEquals("user2@mail.com", users.get(1).getEmail());
    }

    @Test
    @DisplayName("updateUser: должен обновлять пользователя")
    void shouldUpdateUser() {
        User createdUser = userStorage.createUser(user);

        createdUser.setEmail("updated@mail.com");
        createdUser.setLogin("updatedLogin");
        createdUser.setName("Updated Name");
        createdUser.setBirthday(LocalDate.of(1995, 5, 5));

        User updatedUser = userStorage.updateUser(createdUser);

        assertEquals(createdUser.getId(), updatedUser.getId());
        assertEquals("updated@mail.com", updatedUser.getEmail());
        assertEquals("updatedLogin", updatedUser.getLogin());
        assertEquals("Updated Name", updatedUser.getName());
        assertEquals(LocalDate.of(1995, 5, 5), updatedUser.getBirthday());
    }

    @Test
    @DisplayName("updateUser: должен выбрасывать NotFoundException при обновлении несуществующего пользователя")
    void shouldThrowExceptionWhenUpdatingNonExistingUser() {
        User nonExistingUser = new User();
        nonExistingUser.setId(99999L);
        nonExistingUser.setEmail("ghost@mail.com");
        nonExistingUser.setLogin("ghost");
        nonExistingUser.setName("Ghost");
        nonExistingUser.setBirthday(LocalDate.of(1990, 1, 1));

        assertThrows(NotFoundException.class, () -> userStorage.updateUser(nonExistingUser));
    }

    @Test
    @DisplayName("deleteUser: должен удалять пользователя")
    void shouldDeleteUser() {
        User createdUser = userStorage.createUser(user);

        userStorage.deleteUser(createdUser.getId());

        assertThrows(NotFoundException.class,
                () -> userStorage.getUserById(createdUser.getId()));
    }

    @Test
    @DisplayName("deleteUser: должен выбрасывать ValidationException при удалении несуществующего пользователя")
    void shouldThrowExceptionWhenDeletingNonExistingUser() {
        assertThrows(ValidationException.class, () -> userStorage.deleteUser(99999L));
    }

    @Test
    @DisplayName("addFriends: должен добавлять друга")
    void shouldAddFriend() {
        User user1 = new User();
        user1.setEmail("user1@mail.com");
        user1.setLogin("login1");
        user1.setName("User 1");
        user1.setBirthday(LocalDate.of(1990, 1, 1));

        User user2 = new User();
        user2.setEmail("user2@mail.com");
        user2.setLogin("login2");
        user2.setName("User 2");
        user2.setBirthday(LocalDate.of(1992, 2, 2));

        User createdUser1 = userStorage.createUser(user1);
        User createdUser2 = userStorage.createUser(user2);

        userStorage.addFriends(createdUser1.getId(), createdUser2.getId());

        User foundUser = userStorage.getUserById(createdUser1.getId());

        assertTrue(foundUser.getFriends().contains(createdUser2.getId()));
        assertEquals(1, foundUser.getFriends().size());
    }

    @Test
    @DisplayName("deleteFriends: должен удалять друга")
    void shouldDeleteFriend() {
        User user1 = new User();
        user1.setEmail("user1@mail.com");
        user1.setLogin("login1");
        user1.setName("User 1");
        user1.setBirthday(LocalDate.of(1990, 1, 1));

        User user2 = new User();
        user2.setEmail("user2@mail.com");
        user2.setLogin("login2");
        user2.setName("User 2");
        user2.setBirthday(LocalDate.of(1992, 2, 2));

        User createdUser1 = userStorage.createUser(user1);
        User createdUser2 = userStorage.createUser(user2);

        userStorage.addFriends(createdUser1.getId(), createdUser2.getId());
        userStorage.deleteFriends(createdUser1.getId(), createdUser2.getId());

        User foundUser = userStorage.getUserById(createdUser1.getId());

        assertFalse(foundUser.getFriends().contains(createdUser2.getId()));
        assertTrue(foundUser.getFriends().isEmpty());
    }

    @Test
    @DisplayName("getUsersFriends: должен возвращать список друзей пользователя")
    void shouldReturnUsersFriends() {
        User user1 = new User();
        user1.setEmail("user1@mail.com");
        user1.setLogin("login1");
        user1.setName("User 1");
        user1.setBirthday(LocalDate.of(1990, 1, 1));

        User user2 = new User();
        user2.setEmail("user2@mail.com");
        user2.setLogin("login2");
        user2.setName("User 2");
        user2.setBirthday(LocalDate.of(1992, 2, 2));

        User createdUser1 = userStorage.createUser(user1);
        User createdUser2 = userStorage.createUser(user2);

        userStorage.addFriends(createdUser1.getId(), createdUser2.getId());

        List<User> friends = userStorage.getUsersFriends(createdUser1.getId());

        assertEquals(1, friends.size());
        assertEquals(createdUser2.getId(), friends.get(0).getId());
        assertEquals("user2@mail.com", friends.get(0).getEmail());
    }

    @Test
    @DisplayName("existsById: должен возвращать true, если пользователь существует")
    void shouldReturnTrueWhenUserExists() {
        User createdUser = userStorage.createUser(user);

        boolean exists = userStorage.existsById(createdUser.getId());

        assertTrue(exists);
    }

    @Test
    @DisplayName("existsById: должен возвращать false, если пользователь не существует")
    void shouldReturnFalseWhenUserDoesNotExist() {
        boolean exists = userStorage.existsById(99999L);

        assertFalse(exists);
    }

    @Test
    @DisplayName("getCommonFriends: должен возвращать общих друзей")
    void shouldReturnCommonFriends() {
        User user1 = new User();
        user1.setEmail("user1@mail.com");
        user1.setLogin("login1");
        user1.setName("User 1");
        user1.setBirthday(LocalDate.of(1990, 1, 1));

        User user2 = new User();
        user2.setEmail("user2@mail.com");
        user2.setLogin("login2");
        user2.setName("User 2");
        user2.setBirthday(LocalDate.of(1991, 2, 2));

        User commonFriend = new User();
        commonFriend.setEmail("common@mail.com");
        commonFriend.setLogin("commonLogin");
        commonFriend.setName("Common Friend");
        commonFriend.setBirthday(LocalDate.of(1992, 3, 3));

        User createdUser1 = userStorage.createUser(user1);
        User createdUser2 = userStorage.createUser(user2);
        User createdCommonFriend = userStorage.createUser(commonFriend);

        userStorage.addFriends(createdUser1.getId(), createdCommonFriend.getId());
        userStorage.addFriends(createdUser2.getId(), createdCommonFriend.getId());

        List<User> commonFriends = userStorage.getCommonFriends(createdUser1.getId(), createdUser2.getId());

        assertEquals(1, commonFriends.size());
        assertEquals(createdCommonFriend.getId(), commonFriends.get(0).getId());
    }
}