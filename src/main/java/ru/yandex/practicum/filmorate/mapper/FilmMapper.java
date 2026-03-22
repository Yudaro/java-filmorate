package ru.yandex.practicum.filmorate.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

@Component
public class FilmMapper implements RowMapper<Film> {

    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        Film film = new Film();
        Mpa mpa = new Mpa();
        film.setId(rs.getLong("id"));
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));
        film.setReleaseDate(rs.getObject("release", LocalDate.class));
        film.setDuration(rs.getLong("duration"));
        film.setPopular(rs.getLong("popular"));
        long mpaIdFromDb = rs.getLong("mpa_id");

        if (mpaIdFromDb != 0) {
            mpa.setId(mpaIdFromDb);
            film.setMpa(mpa);
        }
        return film;
    }
}
