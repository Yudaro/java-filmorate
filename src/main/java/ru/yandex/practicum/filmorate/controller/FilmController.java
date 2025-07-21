package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;

@Validated
@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    // Возвращает информацию по всем фильмам
    @GetMapping
    public Collection<Film> getFilms() {
        return filmService.filmStorage.findAll();
    }

    @GetMapping("/{filmId}")
    public Film getFilmById(@PathVariable Long filmId) {
        return filmService.filmStorage.getFilmById(filmId);
    }

    // Создает новый фильм
    @PostMapping
    public Film postFilm(@Valid @RequestBody Film film) {
        return filmService.filmStorage.create(film);
    }

    // Обновляет уже существующий фильм
    @PutMapping
    public Film putFilm(@Valid @RequestBody Film newFilm) {
        return filmService.filmStorage.update(newFilm);
    }

    // Ставим фильму лайк
    @PutMapping("/{id}/like/{userId}")
    public Collection<Long> likeFilm(@PathVariable("id") Long filmId, @PathVariable Long userId) {
        return filmService.likeFilm(filmId, userId);
    }

    // Удаляем лайк
    @DeleteMapping("/{id}/like/{userId}")
    public Collection<Long> deleteLike(@PathVariable("id") Long filmId, @PathVariable Long userId) {
        return filmService.deleteLikeFilm(filmId, userId);
    }

    //Выводим переданное количество фильмов от самого популярного. Передается параметр строки count/
    @GetMapping("/popular")
    public Collection<Film> getPopularFilms(@RequestParam(defaultValue = "10") int count) {
        return filmService.filmStorage.getPopularFilms(count);
    }
}
