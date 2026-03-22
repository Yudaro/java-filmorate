package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.db.DbFilmStorage;

import java.time.LocalDate;
import java.time.Month;
import java.util.Collection;

@Service
public class FilmService {
    private final DbFilmStorage filmStorage;


    public FilmService(DbFilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Collection<Film> findAll() {
        return filmStorage.getAllFilms();
    }

    public Film getFilmById(Long filmId) {
        return filmStorage.getFilmById(filmId);
    }

    public Film createFilm(Film film) {
        if (film.getReleaseDate().isAfter(LocalDate.now())) {
            throw new NotFoundException("Дата релиза не может быть в будущем");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, Month.DECEMBER, 28))) {
            throw new ValidationException("Дата релиза вашего фильма не может быть раньше 28 декабря 1895 года. Дата релиза вашего фильма - " + film.getReleaseDate());
        }
        film.setPopular(0L);

        return filmStorage.create(film);
    }

    public Film updateFilm(Film film) {
        if (film.getReleaseDate().isAfter(LocalDate.now())) {
            throw new NotFoundException("Дата релиза не может быть в будущем");
        }

        return filmStorage.update(film);
    }

    public void addLikeForFilm(Long filmId, Long userId) {
        filmStorage.addLikeForFilm(filmId, userId);
    }

    public void deleteLikeForFilm(Long filmId, Long userId) {
        filmStorage.deleteLikeForFilm(filmId, userId);
    }

    public Collection<Film> getPopularFilm(int count) {
        return filmStorage.getPopularFilms(count);
    }
}
