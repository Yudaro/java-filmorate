package ru.yandex.practicum.filmorate.storage.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.MpaMapper;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

@Component
public class DbMpaStorage {

    JdbcTemplate jdbc;
    MpaMapper mapper;

    @Autowired
    public DbMpaStorage(JdbcTemplate jdbc, MpaMapper mapper) {
        this.jdbc = jdbc;
        this.mapper = mapper;
    }

    public List<Mpa> getAllMPA() {
        String query = """
                SELECT * FROM MPA
                order by id
                """;

        List<Mpa> mpa = jdbc.query(query, mapper);
        return mpa;
    }

    public Mpa getMPAById(Long id) {
        String query = """
                SELECT * FROM MPA
                WHERE id = ?
                """;

        try{
            Mpa mpa = jdbc.queryForObject(query, mapper, id);
            return mpa;
        } catch(EmptyResultDataAccessException e){
            throw new NotFoundException("MPA с id - " + id + " не существует.");
        }
    }
}
