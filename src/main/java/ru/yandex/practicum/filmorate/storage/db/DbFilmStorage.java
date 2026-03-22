package ru.yandex.practicum.filmorate.storage.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.*;

@Component
public class DbFilmStorage implements FilmStorage {
    private final FilmMapper mapper;
    private final JdbcTemplate jdbc;
    private final DbUserStorage userStorage;
    private final DbGenreStorage genreStorage;
    private final DbMpaStorage mpaStorage;

    @Autowired
    public DbFilmStorage(FilmMapper mapper, JdbcTemplate jdbc, DbUserStorage userStorage, DbGenreStorage genreStorage,
                         DbMpaStorage mpaStorage) {
        this.mapper = mapper;
        this.jdbc = jdbc;
        this.userStorage = userStorage;
        this.mpaStorage = mpaStorage;
        this.genreStorage = genreStorage;
    }

    public List<Film> getAllFilms() {
        String query = """
                SELECT
                    f.id          AS film_id,
                    f.name        AS film_name,
                    f.description,
                    f.RELEASE,
                    f.duration,
                    f.mpa_id,
                    m.name        AS mpa_name,
                    fg.genre_id,
                    g.name        AS genre_name
                FROM films f
                LEFT JOIN mpa m ON f.mpa_id = m.id
                LEFT JOIN film_genre fg ON fg.film_id = f.id
                LEFT JOIN genres g ON fg.genre_id = g.id
                ORDER BY f.id;
                """;

        return jdbc.query(query, rs -> {
            List<Film> films = new ArrayList<>();

            Film currentFilm = null;
            long lastFilmId = -1;

            while (rs.next()) {
                long filmId = rs.getLong("film_id");

                if (filmId != lastFilmId) {
                    currentFilm = new Film();
                    currentFilm.setId(filmId);
                    currentFilm.setName(rs.getString("film_name"));
                    currentFilm.setDescription(rs.getString("description"));
                    currentFilm.setReleaseDate(rs.getObject("release", LocalDate.class));
                    currentFilm.setDuration(rs.getLong("duration"));

                    long mpaId = rs.getLong("mpa_id");
                    if (!rs.wasNull()) {
                        Mpa mpa = new Mpa();
                        mpa.setId(mpaId);
                        mpa.setName(rs.getString("mpa_name"));
                        currentFilm.setMpa(mpa);
                    }

                    films.add(currentFilm);
                    lastFilmId = filmId;
                }

                long genreId = rs.getLong("genre_id");
                if (!rs.wasNull()) {
                    Genre genre = new Genre();
                    genre.setId(genreId);
                    genre.setName(rs.getString("genre_name"));
                    currentFilm.getGenres().add(genre);
                }
            }

            return films;
        });
    }

    public Film getFilmById(Long id) {
        String query = """
                SELECT 
                    f.id          AS film_id,
                    f.name        AS film_name,
                    f.description,
                    f.RELEASE,
                    f.duration,
                    f.mpa_id,
                    f.popular,
                    m.name        AS mpa_name,
                    fg.genre_id,
                    g.name        AS genre_name
                FROM films f
                LEFT JOIN mpa m ON f.mpa_id = m.id
                LEFT JOIN film_genre fg ON fg.film_id = f.id
                LEFT JOIN genres g ON fg.genre_id = g.id
                WHERE f.id = ?
                ORDER BY g.id; 
                """;

        return jdbc.query(query, rs -> {
            Film film = null;
            boolean filmCreated = false;

            while (rs.next()) {
                long filmId = rs.getLong("film_id");

                if (!filmCreated) {
                    film = new Film();
                    film.setId(filmId);
                    film.setName(rs.getString("film_name"));
                    film.setDescription(rs.getString("description"));
                    film.setReleaseDate(rs.getObject("release", LocalDate.class));
                    film.setDuration(rs.getLong("duration"));
                    film.setPopular(rs.getLong("popular"));
                    film.setLikes(new HashSet<>());
                    film.setGenres(new TreeSet<>(Comparator.comparingLong(Genre::getId)));

                    long mpaId = rs.getLong("mpa_id");
                    if (!rs.wasNull()) {
                        Mpa mpa = new Mpa();
                        mpa.setId(mpaId);
                        mpa.setName(rs.getString("mpa_name"));
                        film.setMpa(mpa);
                    }

                    filmCreated = true;
                }

                long genreId = rs.getLong("genre_id");

                if (!rs.wasNull()) {
                    Genre genre = new Genre();
                    genre.setId(genreId);
                    genre.setName(rs.getString("genre_name"));
                    film.getGenres().add(genre);
                }
            }

            if (film == null) {
                throw new NotFoundException("Фильма с id - " + id + " не существует.");
            }
            return film;
        }, id);
    }

    public Film create(Film film) {
        String query = """
                INSERT INTO films(name, description, release, duration, popular)
                VALUES (?, ?, ?, ?, ?)
                """;

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        int rows = jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setObject(3, film.getReleaseDate());
            ps.setLong(4, film.getDuration());
            ps.setLong(5, film.getPopular());
            return ps;
        }, keyHolder);

        Long newId = keyHolder.getKeyAs(Long.class);

        if (newId == null) {
            throw new RuntimeException("Не удалось получить сгенерированный id");
        }

        film.setId(newId);

        if (film.getMpa() != null) {
            addMpaForFilm(film.getId(), film.getMpa().getId());
            film.setMpa(mpaStorage.getMPAById(film.getMpa().getId()));
        }

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            addGenresForFilm(film);
            film.setGenres(genreStorage.getGenresForFilm(film.getId()));
        }
        return film;
    }

    public Film update(Film newFilm) {
        String query = """
                Update films set
                name = ?,
                description = ?,
                release = ?,
                duration = ?
                Where id = ?
                """;

        int updateRows = jdbc.update(query, newFilm.getName(), newFilm.getDescription(), newFilm.getReleaseDate(), newFilm.getDuration(), newFilm.getId());

        if (updateRows == 0) {
            throw new NotFoundException("Фильм с id - " + newFilm.getId() + " не найден.");
        }

        return getFilmById(newFilm.getId());
    }

    public void addLikeForFilm(Long filmId, Long userId) {
        String query = """
                INSERT INTO likes(film_id, user_id)
                VALUES(?, ?)
                """;

        getFilmById(filmId);
        userStorage.getUserById(userId);

        int rows = jdbc.update(query, filmId, userId);

        Long filmPopular = getPopularPoint(filmId);
        filmPopular++;
        setPopularFilm(filmId, filmPopular);
    }

    public void deleteLikeForFilm(Long filmId, Long userId) {
        String query = """
                Delete FROM likes
                WHERE film_id = ?
                AND user_id = ?
                """;

        getFilmById(filmId);
        userStorage.getUserById(userId);

        int rows = jdbc.update(query, filmId, userId);

        Long filmPopular = getPopularPoint(filmId);
        filmPopular--;
        setPopularFilm(filmId, filmPopular);
    }

    public List<Film> getPopularFilms(int count) {
        String query = """
                SELECT *
                FROM films
                ORDER BY popular DESC
                LIMIT ?
                """;

        List<Film> popularFilms = jdbc.query(query, mapper, count);
        return popularFilms;
    }

    public Long getPopularPoint(Long filmId) {
        String query = """
                SELECT popular
                FROM films
                Where id = ?
                """;

        Long popular = jdbc.queryForObject(query, Long.class, filmId);
        return popular;
    }

    public void setPopularFilm(Long filmId, Long popular) {
        String query = """
                UPDATE films set
                popular = ?
                WHERE id = ?
                """;

        int rows = jdbc.update(query, popular, filmId);

        if (rows == 0) {
            throw new NotFoundException("Счетчик популярности не изменился.");
        }
    }

    public void addMpaForFilm(Long filmId, Long mpaId) {
        String query = """
                UPDATE films set
                Mpa_id = ?
                WHERE id = ?
                """;
        mpaStorage.getMPAById(mpaId);
        int rows = jdbc.update(query, mpaId, filmId);
    }

    public void addGenreForFilm(Long filmId, Long genreId) {
        String query = """
                INSERT INTO film_genre(film_id, genre_id)
                VALUES(?, ?)
                """;
        genreStorage.getGenreById(genreId);
        int rows = jdbc.update(query, filmId, genreId);
    }

    private void addGenresForFilm(Film film) {
        film.getGenres().stream()
                .map(Genre::getId)
                .filter(Objects::nonNull)
                .forEach(genreId -> addGenreForFilm(film.getId(), genreId));
    }
}