package ru.yandex.practicum.filmorate.storage.db;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.*;

@Component
public class DbGenreStorage {

    private final JdbcTemplate jdbc;
    private final GenreMapper mapper;

    public DbGenreStorage(JdbcTemplate jdbc, GenreMapper mapper) {
        this.jdbc = jdbc;
        this.mapper = mapper;
    }

    public List<Genre> getAllGenres() {
        String query = """
                Select * from genres
                Order by id
                """;
        List<Genre> genres = jdbc.query(query, mapper);
        return genres;
    }

    public Genre getGenreById(Long id) {
        String query = """
                SELECT * FROM genres
                WHERE id = ?
                """;
        try {
            Genre genre = jdbc.queryForObject(query, mapper, id);
            return genre;
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Жанра с id - " + id + " не существует");
        }
    }

    public Set<Genre> getGenresForFilm(Long filmId) {
        String query = """
                SELECT g.id, g.name
                FROM film_genre AS fg
                JOIN genres AS g ON fg.genre_id = g.id
                WHERE fg.film_id = ?
                ORDER BY g.id  
                """;

        List<Genre> genresList = jdbc.query(query, mapper, filmId);
        Set<Genre> genresSet = new TreeSet<>(Comparator.comparingLong(Genre::getId));
        genresSet.addAll(genresList);

        return genresSet;
    }
}