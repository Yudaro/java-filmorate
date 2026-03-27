package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.db.DbFilmStorage;
import ru.yandex.practicum.filmorate.storage.db.DbGenreStorage;
import ru.yandex.practicum.filmorate.storage.db.DbMpaStorage;
import ru.yandex.practicum.filmorate.storage.db.DbUserStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class DbFilmStorageTest {

    @Autowired
    private DbFilmStorage filmStorage;

    @Autowired
    private JdbcTemplate jdbc;

    @Autowired
    private DbUserStorage userStorage;

    @Autowired
    private DbGenreStorage genreStorage;

    @Autowired
    private DbMpaStorage mpaStorage;

    private Film film;
    private User user;

    @BeforeEach
    void setUp() {
        jdbc.update("DELETE FROM likes");
        jdbc.update("DELETE FROM film_genre");
        jdbc.update("DELETE FROM friendship");
        jdbc.update("DELETE FROM films");
        jdbc.update("DELETE FROM users");

        film = new Film();
        film.setName("Интерстеллар");
        film.setDescription("Фантастический фильм");
        film.setReleaseDate(LocalDate.of(2014, 11, 7));
        film.setDuration(169L);
        film.setPopular(0L);

        Mpa mpa = new Mpa();
        mpa.setId(1L);
        film.setMpa(mpa);

        Set<Genre> genres = new TreeSet<>(Comparator.comparingLong(Genre::getId));
        Genre genre = new Genre();
        genre.setId(2L);
        genres.add(genre);
        film.setGenres(genres);

        user = new User();
        user.setEmail("test@mail.com");
        user.setLogin("testLogin");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        user = userStorage.createUser(user); // или create(user), как у тебя называется
    }

    @Test
    @DisplayName("create: должен сохранять фильм в БД")
    void shouldCreateFilm() {
        Film createdFilm = filmStorage.create(film);

        assertNotNull(createdFilm.getId(), "ID фильма должен быть сгенерирован");
        assertEquals("Интерстеллар", createdFilm.getName());
        assertEquals("Фантастический фильм", createdFilm.getDescription());
        assertEquals(LocalDate.of(2014, 11, 7), createdFilm.getReleaseDate());
        assertEquals(169L, createdFilm.getDuration());
        assertNotNull(createdFilm.getMpa());
        assertEquals(1L, createdFilm.getMpa().getId());
        assertFalse(createdFilm.getGenres().isEmpty());
    }

    @Test
    @DisplayName("getFilmById: должен возвращать фильм по id")
    void shouldReturnFilmById() {
        Film createdFilm = filmStorage.create(film);

        Film foundFilm = filmStorage.getFilmById(createdFilm.getId());

        assertEquals(createdFilm.getId(), foundFilm.getId());
        assertEquals(createdFilm.getName(), foundFilm.getName());
        assertEquals(createdFilm.getDescription(), foundFilm.getDescription());
        assertEquals(createdFilm.getReleaseDate(), foundFilm.getReleaseDate());
        assertEquals(createdFilm.getDuration(), foundFilm.getDuration());
        assertNotNull(foundFilm.getMpa());
        assertEquals(1L, foundFilm.getMpa().getId());
        assertEquals(1, foundFilm.getGenres().size());
    }

    @Test
    @DisplayName("getFilmById: должен выбрасывать NotFoundException для несуществующего фильма")
    void shouldThrowExceptionWhenFilmNotFound() {
        assertThrows(NotFoundException.class, () -> filmStorage.getFilmById(99999L));
    }

    @Test
    @DisplayName("getAllFilms: должен возвращать список фильмов")
    void shouldReturnAllFilms() {
        Film film1 = new Film();
        film1.setName("Фильм 1");
        film1.setDescription("Описание 1");
        film1.setReleaseDate(LocalDate.of(2020, 1, 1));
        film1.setDuration(100L);
        film1.setPopular(0L);

        Film film2 = new Film();
        film2.setName("Фильм 2");
        film2.setDescription("Описание 2");
        film2.setReleaseDate(LocalDate.of(2021, 2, 2));
        film2.setDuration(120L);
        film2.setPopular(0L);

        filmStorage.create(film1);
        filmStorage.create(film2);

        List<Film> films = filmStorage.getAllFilms();

        assertNotNull(films);
        assertTrue(films.size() >= 2);
    }

    @Test
    @DisplayName("update: должен обновлять данные фильма")
    void shouldUpdateFilm() {
        Film createdFilm = filmStorage.create(film);

        createdFilm.setName("Обновлённый фильм");
        createdFilm.setDescription("Новое описание");
        createdFilm.setReleaseDate(LocalDate.of(2022, 5, 5));
        createdFilm.setDuration(150L);

        Film updatedFilm = filmStorage.update(createdFilm);

        assertEquals(createdFilm.getId(), updatedFilm.getId());
        assertEquals("Обновлённый фильм", updatedFilm.getName());
        assertEquals("Новое описание", updatedFilm.getDescription());
        assertEquals(LocalDate.of(2022, 5, 5), updatedFilm.getReleaseDate());
        assertEquals(150L, updatedFilm.getDuration());
    }

    @Test
    @DisplayName("update: должен выбрасывать NotFoundException при обновлении несуществующего фильма")
    void shouldThrowExceptionWhenUpdatingNonExistingFilm() {
        Film nonExistingFilm = new Film();
        nonExistingFilm.setId(99999L);
        nonExistingFilm.setName("Нет такого фильма");
        nonExistingFilm.setDescription("Описание");
        nonExistingFilm.setReleaseDate(LocalDate.of(2020, 1, 1));
        nonExistingFilm.setDuration(100L);

        assertThrows(NotFoundException.class, () -> filmStorage.update(nonExistingFilm));
    }

    @Test
    @DisplayName("addLikeForFilm: должен добавлять лайк и увеличивать popular")
    void shouldAddLikeForFilm() {
        Film createdFilm = filmStorage.create(film);

        filmStorage.addLikeForFilm(createdFilm.getId(), user.getId());

        Film foundFilm = filmStorage.getFilmById(createdFilm.getId());
        Long popular = filmStorage.getPopularPoint(createdFilm.getId());

        assertEquals(1L, popular);
        assertNotNull(foundFilm);
    }

    @Test
    @DisplayName("deleteLikeForFilm: должен удалять лайк и уменьшать popular")
    void shouldDeleteLikeForFilm() {
        Film createdFilm = filmStorage.create(film);
        filmStorage.addLikeForFilm(createdFilm.getId(), user.getId());

        filmStorage.deleteLikeForFilm(createdFilm.getId(), user.getId());

        Long popular = filmStorage.getPopularPoint(createdFilm.getId());
        assertEquals(0L, popular);
    }

    @Test
    @DisplayName("getPopularFilms: должен возвращать фильмы, отсортированные по popular")
    void shouldReturnPopularFilms() {
        Film film1 = new Film();
        film1.setName("Фильм 1");
        film1.setDescription("Описание 1");
        film1.setReleaseDate(LocalDate.of(2020, 1, 1));
        film1.setDuration(100L);
        film1.setPopular(0L);

        Film film2 = new Film();
        film2.setName("Фильм 2");
        film2.setDescription("Описание 2");
        film2.setReleaseDate(LocalDate.of(2021, 2, 2));
        film2.setDuration(120L);
        film2.setPopular(0L);

        Film createdFilm1 = filmStorage.create(film1);
        Film createdFilm2 = filmStorage.create(film2);

        filmStorage.addLikeForFilm(createdFilm2.getId(), user.getId());

        List<Film> popularFilms = filmStorage.getPopularFilms(10);

        assertNotNull(popularFilms);
        assertFalse(popularFilms.isEmpty());
        assertEquals(createdFilm2.getId(), popularFilms.get(0).getId());
    }
}
