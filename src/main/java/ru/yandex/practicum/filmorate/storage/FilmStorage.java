package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

//Интерфейс для работы с фильмами (добавление, удаление, модификация объектов)
public interface FilmStorage {

    Film create(Film film);

    Film delete(Film film);

    Film update(Film newFilm);

    long getNextId();

    Collection<Film> findAll();

    Film getFilmById(Long filmId);

    Collection<Film> getPopularFilms(int count);
}
