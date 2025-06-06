-- Удаление старых таблиц, если они существуют
DROP TABLE IF EXISTS film_likes;
DROP TABLE IF EXISTS film_genres;
DROP TABLE IF EXISTS user_friends;
DROP TABLE IF EXISTS films;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS genres;
DROP TABLE IF EXISTS mpa;

-- Таблица рейтингов MPA
CREATE TABLE mpa (
                     id INTEGER PRIMARY KEY,
                     name VARCHAR(50) NOT NULL
);

-- Таблица жанров
CREATE TABLE genres (
                        id INTEGER PRIMARY KEY,
                        name VARCHAR(50) NOT NULL
);

-- Таблица пользователей
CREATE TABLE users (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       login VARCHAR(255) NOT NULL UNIQUE,
                       name VARCHAR(255),
                       birthday DATE NOT NULL
);

-- Таблица фильмов
CREATE TABLE films (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       name VARCHAR(255) NOT NULL,
                       description VARCHAR(200),
                       release_date DATE NOT NULL,
                       duration INTEGER NOT NULL CHECK (duration > 0),
                       mpa_id INTEGER REFERENCES mpa(id)
);

-- Лайки фильмам
CREATE TABLE film_likes (
                            film_id BIGINT REFERENCES films(id) ON DELETE CASCADE,
                            user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
                            PRIMARY KEY (film_id, user_id)
);

-- Жанры у фильмов
CREATE TABLE film_genres (
                             film_id BIGINT REFERENCES films(id) ON DELETE CASCADE,
                             genre_id INTEGER REFERENCES genres(id) ON DELETE CASCADE,
                             PRIMARY KEY (film_id, genre_id)
);

-- Друзья
CREATE TABLE user_friends (
                              user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
                              friend_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
                              PRIMARY KEY (user_id, friend_id)
);