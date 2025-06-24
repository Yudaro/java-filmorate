package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.Month;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Service
public class FilmService {
    private Map<Long, Film> films = new HashMap<>();
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);

    public Collection<Film> findAll() {
        log.info("Начало выполнения метода getFilms(Возвращаем все фильмы)");
        return films.values();
    }

    public Film create(Film film) {
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, Month.DECEMBER, 28))) {
            log.warn("Дата релиза фильма не может быть раньше 28 декабря 1895 года");
            throw new ValidationException("Дата релиза вашего фильма не может быть раньше 28 декабря 1895 года. Дата релиза вашего фильма - " + film.getReleaseDate());
        }

        log.info("Метод postFilm отработал корректно, начался процесс добавления нового фильма");
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Новый фильм добавлен");
        return film;
    }

    public Film update(Film newFilm) {
        log.info("Начало выполнения метода putFilm(Изменение фильма)");
        if (newFilm.getId() == null) {
            log.warn("Отсутствует id фильма который необходимо изменить");
            throw new ValidationException("Id - должен быть указан");
        }

        if (!films.containsKey(newFilm.getId())) {
            log.warn("В программе нет фильма с таким id - " + newFilm.getId());
            throw new ValidationException("Некорректный id фильма - " + newFilm.getId());
        }

        Film oldFilm = films.get(newFilm.getId());
        log.warn("Фильм который необходимо обновить найден");

        if (newFilm.getName() == null) {
            log.info("name - не будет изменен");
            newFilm.setName(oldFilm.getName());
        }

        if (newFilm.getDescription() == null || newFilm.getDescription().length() > 200) {
            log.info("description - не будет изменен. Значение не передано, либо превышает 200 символов");
            newFilm.setDescription(oldFilm.getDescription());
        }

        if (newFilm.getReleaseDate() == null || newFilm.getReleaseDate().isBefore(LocalDate.of(1895, Month.DECEMBER, 28))) {
            log.info("releaseDate - не будет изменен. Значение не передано, либо дата релиза фильма раньше  28 декабря 1895 года");
            newFilm.setReleaseDate(oldFilm.getReleaseDate());
        }

        if (newFilm.getDuration() == null || newFilm.getDuration() <= 0) {
            log.info("duration - не будет изменен. Значение не передано, либо значение меньше 0");
            newFilm.setDuration(oldFilm.getDuration());
        }

        films.put(newFilm.getId(), newFilm);
        log.info("Фильм обновлен");
        return newFilm;
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
