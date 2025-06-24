package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Service
public class UserTest {
    private final Validator validator;
    private Set<ConstraintViolation<User>> violations;
    private User user;

    private UserTest() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @BeforeEach
    public void setUp() {
        violations = new HashSet<>();
        user = new User();
        user.setEmail("Alexander@gmail.com");
        user.setLogin("Yudaro");
        user.setBirthday(LocalDate.of(1999, 9, 17));
    }

    @Test
    public void emailFieldCannotBeEmpty() {
        user.setEmail(" ");
        violations = validator.validate(user);
        assertEquals(2, violations.size());
    }

    @Test
    public void emailFieldCannotBeNull() {
        user.setEmail(null);
        violations = validator.validate(user);
        assertEquals(1, violations.size());
    }

    @Test
    public void birthdayFieldCannotBeFuture() {
        user.setBirthday(LocalDate.now());
        violations = validator.validate(user);
        assertEquals(1, violations.size());
    }

    @Test
    public void logginFieldCannotBeNull() {
        user.setLogin(null);
        violations = validator.validate(user);
        assertEquals(1, violations.size());
    }
}
