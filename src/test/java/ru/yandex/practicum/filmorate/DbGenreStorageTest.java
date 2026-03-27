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
import ru.yandex.practicum.filmorate.storage.db.DbFilmStorage;
import ru.yandex.practicum.filmorate.storage.db.DbGenreStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class DbGenreStorageTest {

    @Autowired
    private DbGenreStorage genreStorage;

    @Autowired
    private DbFilmStorage filmStorage;

    @Autowired
    private JdbcTemplate jdbc;

    @BeforeEach
    void setUp() {
        jdbc.update("DELETE FROM likes");
        jdbc.update("DELETE FROM film_genre");
        jdbc.update("DELETE FROM friendship");
        jdbc.update("DELETE FROM films");
        jdbc.update("DELETE FROM users");
    }

    @Test
    @DisplayName("getAllGenres: должен возвращать все жанры")
    void shouldReturnAllGenres() {
        List<Genre> genres = genreStorage.getAllGenres();

        assertNotNull(genres);
        assertFalse(genres.isEmpty());
        assertEquals(6, genres.size());
        assertEquals(1L, genres.get(0).getId());
        assertEquals("Комедия", genres.get(0).getName());
    }

    @Test
    @DisplayName("getGenreById: должен возвращать жанр по id")
    void shouldReturnGenreById() {
        Genre genre = genreStorage.getGenreById(1L);

        assertNotNull(genre);
        assertEquals(1L, genre.getId());
        assertEquals("Комедия", genre.getName());
    }

    @Test
    @DisplayName("getGenreById: должен выбрасывать NotFoundException для несуществующего жанра")
    void shouldThrowExceptionWhenGenreNotFound() {
        assertThrows(NotFoundException.class, () -> genreStorage.getGenreById(999L));
    }

    @Test
    @DisplayName("getGenresForFilm: должен возвращать жанры фильма")
    void shouldReturnGenresForFilm() {
        Film film = new Film();
        film.setName("Тестовый фильм");
        film.setDescription("Описание");
        film.setReleaseDate(LocalDate.of(2020, 1, 1));
        film.setDuration(120L);
        film.setPopular(0L);

        Genre genre1 = new Genre();
        genre1.setId(1L);

        Genre genre2 = new Genre();
        genre2.setId(2L);

        film.getGenres().add(genre1);
        film.getGenres().add(genre2);

        Film createdFilm = filmStorage.create(film);

        Set<Genre> genres = genreStorage.getGenresForFilm(createdFilm.getId());

        assertNotNull(genres);
        assertEquals(2, genres.size());

        Genre firstGenre = genres.iterator().next();
        assertEquals(1L, firstGenre.getId());
    }

    @Test
    @DisplayName("getGenresForFilm: должен возвращать пустой набор, если у фильма нет жанров")
    void shouldReturnEmptySetWhenFilmHasNoGenres() {
        Film film = new Film();
        film.setName("Фильм без жанров");
        film.setDescription("Описание");
        film.setReleaseDate(LocalDate.of(2021, 2, 2));
        film.setDuration(90L);
        film.setPopular(0L);

        Film createdFilm = filmStorage.create(film);

        Set<Genre> genres = genreStorage.getGenresForFilm(createdFilm.getId());

        assertNotNull(genres);
        assertTrue(genres.isEmpty());
    }
}