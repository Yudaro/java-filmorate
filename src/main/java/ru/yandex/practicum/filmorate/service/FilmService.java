package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import java.util.Collection;

@Service
public class FilmService {
    public FilmStorage filmStorage;
    private UserService userService;

    @Autowired
    public FilmService(InMemoryFilmStorage filmStorage, UserService userService) {
        this.filmStorage = filmStorage;
        this.userService = userService;
    }

    public Collection<Long> likeFilm(Long filmId, Long userId) {
        Film film = filmStorage.getFilmById(filmId);
        userService.userStorage.getUserById(userId);
        film.likeFilm(userId);
        return film.getLikes();
    }

    public Collection<Long> deleteLikeFilm(Long filmId, Long userId) {
        Film film = filmStorage.getFilmById(filmId);
        userService.userStorage.getUserById(userId);
        film.deleteLike(userId);
        return film.getLikes();
    }
}
