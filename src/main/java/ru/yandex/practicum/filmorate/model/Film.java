package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.yandex.practicum.filmorate.validation.CreateGroup;
import ru.yandex.practicum.filmorate.validation.UpdateGroup;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Data
public class Film {
    @Null(message = "Поле id - должно быть пустым при создании.", groups = CreateGroup.class)
    @NotBlank(message = "Поле id - не должно быть пустым при обновлении.", groups = UpdateGroup.class)
    private Long id;
    @NotBlank(message = "name(название фильма) - не может быть пустым.")
    private String name;
    @Size(max = 200, message = "description(описание фильма) - не должно содержать больше 200 символов.")
    private String description;
    private LocalDate releaseDate;
    @Positive(message = "duration(продолжительность фильма в минутых) - не может быть отрицательным или равна 0.")
    private Long duration;
    private Set<Long> likes = new HashSet<>();

    public void likeFilm(Long userId) {
        likes.add(userId);
    }

    public void deleteLike(Long userId) {
        likes.remove(userId);
    }

    public Set<Long> getLikes() {
        return Collections.unmodifiableSet(likes);
    }
}
