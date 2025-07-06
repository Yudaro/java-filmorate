package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
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

    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    // Возвращает информацию по всем фильмам
    @GetMapping
    public Collection<Film> getFilms() {
        return filmService.findAll();
    }

    // Создает новый фильм
    @PostMapping
    public Film postFilm(@Valid @RequestBody Film film) {
        return filmService.create(film);
    }

    // Обновляет уже существующий фильм
    @PutMapping
    public Film putFilm(@Valid @RequestBody Film newFilm) {
        return filmService.update(newFilm);
    }
}
