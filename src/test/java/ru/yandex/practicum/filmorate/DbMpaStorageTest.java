package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.db.DbMpaStorage;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class DbMpaStorageTest {

    @Autowired
    private DbMpaStorage mpaStorage;

    @Test
    @DisplayName("getAllMPA: должен возвращать все рейтинги MPA")
    void shouldReturnAllMpa() {
        List<Mpa> mpaList = mpaStorage.getAllMPA();

        assertNotNull(mpaList);
        assertFalse(mpaList.isEmpty());
        assertEquals(5, mpaList.size());

        assertEquals(1L, mpaList.get(0).getId());
        assertEquals("G", mpaList.get(0).getName());
    }

    @Test
    @DisplayName("getMPAById: должен возвращать MPA по id")
    void shouldReturnMpaById() {
        Mpa mpa = mpaStorage.getMPAById(1L);

        assertNotNull(mpa);
        assertEquals(1L, mpa.getId());
        assertEquals("G", mpa.getName());
    }

    @Test
    @DisplayName("getMPAById: должен выбрасывать NotFoundException для несуществующего id")
    void shouldThrowExceptionWhenMpaNotFound() {
        assertThrows(NotFoundException.class, () -> mpaStorage.getMPAById(999L));
    }
}