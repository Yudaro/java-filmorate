package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.groups.Default;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.validation.UpdateGroup;

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
        return filmService.findAll();
    }

    // Ищем фильм по id
    @GetMapping("/{filmId}")
    public Film getFilmById(@PathVariable Long filmId) {
        return filmService.getFilmById(filmId);
    }

    // Создает новый фильм
    @PostMapping
    public Film postFilm(@Valid @RequestBody Film film) {
        return filmService.createFilm(film);
    }

    // Обновляет уже существующий фильм
    @PutMapping
    public Film putFilm(@Validated({UpdateGroup.class, Default.class}) @RequestBody Film newFilm) {
        return filmService.updateFilm(newFilm);
    }

    // Ставим фильму лайк
    @PutMapping("/{id}/like/{userId}")
    public void likeFilm(@PathVariable("id") Long filmId, @PathVariable Long userId) {
        filmService.addLikeForFilm(filmId, userId);
    }

    // Удаляем лайк
    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable("id") Long filmId, @PathVariable Long userId) {
        filmService.deleteLikeForFilm(filmId, userId);
    }

    //Выводим переданное количество фильмов от самого популярного. Передается параметр строки count/
    @GetMapping("/popular")
    public Collection<Film> getPopularFilms(@RequestParam(defaultValue = "10") int count) {
        return filmService.getPopularFilm(count);
    }
}