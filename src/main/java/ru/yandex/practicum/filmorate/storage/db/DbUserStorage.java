package ru.yandex.practicum.filmorate.storage.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class DbUserStorage implements UserStorage {

    private final UserMapper mapper;
    private final JdbcTemplate jdbc;

    @Autowired
    public DbUserStorage(UserMapper mapper, JdbcTemplate jdbc) {
        this.mapper = mapper;
        this.jdbc = jdbc;
    }

    // Получаем из БД список всех пользователей
    public List<User> getAllUser() {
        String query  = """
                SELECT u.id, u.email, u.login, u.name, u.birthday, fs.friend_id
                FROM users as u
                LEFT JOIN friendship as fs on u.id = fs.user_id
                Order by u.id
                """;

        return jdbc.query(query, rs ->{
            List<User> users = new ArrayList<>();

            User currentUser = null;
            long lastUserId = -1;

            while(rs.next()) {
                long userId = rs.getLong("id");

                if (userId != lastUserId) {
                    currentUser = new User();
                    currentUser.setId(rs.getLong("id"));
                    currentUser.setEmail(rs.getString("email"));
                    currentUser.setLogin(rs.getString("login"));
                    currentUser.setName(rs.getString("name"));
                    currentUser.setBirthday(rs.getObject("birthday", LocalDate.class));

                    users.add(currentUser);
                    lastUserId = userId;
                }

                long friendId = rs.getLong("friend_id");
                if (!rs.wasNull()) {
                    currentUser.getFriends().add(friendId);
                }
            }
            return users;
        });
    }

    // Получаем пользователя по id
    public User getUserById(Long id) {
        String query = """
                SELECT u.id, u.email, u.login, u.name, u.birthday, fs.friend_id
                FROM users as u
                LEFT JOIN friendship as fs on u.id = fs.user_id
                WHERE id = ?
                """;
        try {
            return jdbc.query(query, rs -> {
                User user = null;
                while(rs.next()) {
                    if (user == null) {
                        user = new User();
                        user.setId(rs.getLong("id"));
                        user.setEmail(rs.getString("email"));
                        user.setLogin(rs.getString("login"));
                        user.setName(rs.getString("name"));
                        user.setBirthday(rs.getObject(("birthday"), LocalDate.class));
                    }

                    long friendId = rs.getLong("friend_id");
                    if (friendId != 0) {
                        user.getFriends().add(friendId);
                    }
                }
                return user;
            }, id);
        } catch(EmptyResultDataAccessException e) {
            throw new NotFoundException("Пользователя с id - " + id + " не существует.");
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new IllegalStateException("Найдено больше одной записи для id " + id);
        }
    }

    public User createUser(User user) {
        String query = """
                INSERT INTO users(email, login, name, birthday)
                VALUES(?,?,?,?)
                """;
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getName());
            ps.setObject(4, user.getBirthday());
            return ps;
        }, keyHolder);

        Long newId = keyHolder.getKeyAs(Long.class);

        if(newId == null) {
            throw new RuntimeException("Не удалось получить сгенерированный id");
        }

        user.setId(newId);
        return user;
    }

    public User updateUser(User user) {
        String query = """
                Update users set
                email = ?,
                login = ?,
                name = ?,
                birthday = ?
                Where id = ?
                """;

        int updateRows = jdbc.update(query, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());

        if (updateRows == 0) {
            throw new NotFoundException("Пользователь с id - " + user.getId() + " не найден." );
        }
        return getUserById(user.getId());
    }

    public void deleteUser(Long id) {
        String query = """
                Delete from users
                where id = ?
                """;

        int updateRows = jdbc.update(query, id);

        if (updateRows == 0) {
            throw new ValidationException("Пользователь с id - " + id + " не найден." );
        }
    }

    public void addFriends(Long userId, Long friendId) {
        getUserById(userId);
        getUserById(friendId);

        String query = """
                Insert Into FRIENDSHIP(user_id, friend_id)
                VALUES(?, ?)
                """;

        int rows = jdbc.update(query, userId, friendId);
    }

    public void deleteFriends(Long userId, Long friendId) {
        getUserById(userId);
        getUserById(friendId);

        String query = """
                DELETE from FRIENDSHIP
                where user_id = ?
                and friend_id = ?
                """;

        int rows = jdbc.update(query, userId, friendId);
    }

    public List<User> getCommonFriends(Long userId, Long otherUserId) {
        String query = """
        SELECT
            u.id,
            u.email,
            u.login,
            u.name,
            u.birthday,
        FROM users u
        INNER JOIN friendship fs1 ON u.id = fs1.friend_id
        INNER JOIN friendship fs2 ON u.id = fs2.friend_id
        WHERE fs1.user_id = ?
          AND fs2.user_id = ?
        ORDER BY u.id
        """;

        getUserById(userId);
        getUserById(otherUserId);

        List<User> users = jdbc.query(query, mapper, userId, otherUserId);
        return users;
    }

    public List<User> getUsersFriends(long userId) {
        String query = """
                SELECT
                    u.id,
                    u.email,
                    u.login,
                    u.name,
                    u.birthday
                FROM users u
                INNER JOIN friendship fs ON u.id = fs.friend_id
                WHERE fs.user_id = ?
                ORDER BY u.id
                """;

        return jdbc.query(query, (rs, rowNum) -> {
            User friend = new User();
            friend.setId(rs.getLong("id"));
            friend.setEmail(rs.getString("email"));
            friend.setLogin(rs.getString("login"));
            friend.setName(rs.getString("name"));
            friend.setBirthday(rs.getObject("birthday", LocalDate.class));
            return friend;
        }, userId);
    }

    public boolean existsById(long id) {
        String query = "SELECT 1 FROM users WHERE id = ?";
        try {
            jdbc.queryForObject(query, Integer.class, id);
            return true;
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
    }
}