package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;

//Интерфейс для работы с фильмами (добавление, удаление, модификация объектов)
public interface FilmStorage {

    Film create(Film film);

    Film delete(Film film);

    Film update(Film newFilm);

    long getNextId();

    Collection<Film> findAll();

    Film getFilmById(Long filmId);

    List<Film> getPopularFilms(int count);
}
