package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Service
public class FilmTest {
    private final Validator validator;
    private Set<ConstraintViolation<Film>> violations;
    private Film film;

    private FilmTest() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @BeforeEach
    public void setUp() {
        violations = new HashSet<>();
        film = new Film();
        film.setName("Собачье сердце");
        film.setDescription("Профессор Преображенский превращает уличного пса в человека.");
        film.setReleaseDate(LocalDate.of(1988,11,20));
        film.setDuration(8160L);
    }

    @Test
    public void nameIsNotBlankValidation() {
        film.setName("");
        violations = validator.validate(film);
        assertEquals(1, violations.size());
    }

    @Test
    public void descriptionSizeValidation() {
        film.setDescription("A".repeat(201));
        violations = validator.validate(film);
        assertEquals(1, violations.size());
    }

    @Test
    public void negativeDurationValidation() {
        film.setDuration(-1L);
        violations = validator.validate(film);
        assertEquals(1, violations.size());
    }

    @Test
    public void validFilm() {
        violations = validator.validate(film);
        assertTrue(violations.isEmpty());
    }
}
