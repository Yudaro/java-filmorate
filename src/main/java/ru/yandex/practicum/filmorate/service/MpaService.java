package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.db.DbMpaStorage;

import java.util.List;

@Service
public class MpaService {
    private final DbMpaStorage mpaStorage;

    @Autowired
    public MpaService(DbMpaStorage mpaStorage) {
        this.mpaStorage = mpaStorage;
    }

    public List<Mpa> getAllMpa() {
        return mpaStorage.getAllMPA();
    }

    public Mpa getMpaById(Long id) {
        return mpaStorage.getMPAById(id);
    }
}
