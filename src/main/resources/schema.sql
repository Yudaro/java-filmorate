-- Удаляем таблицы в ПРАВИЛЬНОМ порядке (сначала дочерние, затем родительские)
DROP TABLE IF EXISTS likes;
DROP TABLE IF EXISTS film_genre;
DROP TABLE IF EXISTS friendship;
DROP TABLE IF EXISTS films;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS genres;
DROP TABLE IF EXISTS mpa;

CREATE TABLE mpa
(
    id   BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(5),
    CONSTRAINT uq_mpa_name UNIQUE (name)
);

CREATE TABLE genres
(
    id   BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(30) NOT NULL,
    CONSTRAINT uq_genre_name UNIQUE (name)
);

CREATE TABLE users
(
    id       BIGINT PRIMARY KEY AUTO_INCREMENT,
    email    VARCHAR(255) NOT NULL,
    login    VARCHAR(30)  NOT NULL,
    name     VARCHAR(255),
    birthday DATE,
    CONSTRAINT uq_user_email UNIQUE (email)
);

CREATE TABLE films
(
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    name        VARCHAR(255) NOT NULL,
    description VARCHAR(200),
    release     DATE,
    duration    INT,
    mpa_id      INT,
    popular     INT,
    FOREIGN KEY (mpa_id) REFERENCES mpa (id)
);

CREATE TABLE friendship
(
    user_id   BIGINT NOT NULL,
    friend_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, friend_id),
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    FOREIGN KEY (friend_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE film_genre
(
    film_id  BIGINT NOT NULL,
    genre_id BIGINT NOT NULL,
    PRIMARY KEY (film_id, genre_id),
    FOREIGN KEY (film_id) REFERENCES films (id) ON DELETE CASCADE,
    FOREIGN KEY (genre_id) REFERENCES genres (id)
);

CREATE TABLE likes
(
    film_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    PRIMARY KEY (film_id, user_id),
    FOREIGN KEY (film_id) REFERENCES films (id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

INSERT INTO mpa (name) VALUES
('G'),
('PG'),
('PG-13'),
('R'),
('NC-17');


INSERT INTO genres (name) VALUES
('Комедия'),
('Драма'),
('Мультфильм'),
('Триллер'),
('Документальный'),
('Боевик');