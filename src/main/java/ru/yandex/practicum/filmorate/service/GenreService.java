package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.db.DbGenreStorage;

import java.util.List;

@Service
public class GenreService {
    private final DbGenreStorage genreStrorage;

    @Autowired
    public GenreService(DbGenreStorage genreStorage) {
        this.genreStrorage = genreStorage;
    }

    public List<Genre> getAllGenre() {
        return genreStrorage.getAllGenres();
    }

    public Genre getGenreById(Long id) {
        return genreStrorage.getGenreById(id);
    }
}
