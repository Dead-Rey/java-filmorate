package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeption.AlreadyExistsException;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

@Service
@AllArgsConstructor
@Getter
@Slf4j
public class UserService {

    private List<User> users;
    private static Long idCounter = 1L;

    public User createUser(User user) {
        log.info("Создание пользователя: {}", user.getLogin());
        for (User u : users) {
            if (u.getEmail().equals(user.getEmail())) {
                log.error("Пользователь с email: {} уже существует", user.getEmail());
                throw new AlreadyExistsException("Данный email уже зарегистрирован");
            }
            if (u.getLogin().equals(user.getLogin())) {
                log.error("Пользователь с логином: {} уже существует", user.getLogin());
                throw new AlreadyExistsException("Данный логин уже зарегистрирован");
            }
            if (user.getName() == null || user.getName().isBlank()) {
                log.debug("Имя пользователя не введено. Использован логин: {}", user.getLogin());
                user.setName(user.getLogin());
            }
        }
        if (user.getId() == null) {
            user.setId(idCounter++);
        }
        users.add(user);
        log.info("Пользователь: {} успешно создан", user.getLogin());
        return user;
    }

    public User updateUser(User user) {
        log.info("Обновление пользователя с id: {}", user.getId());
        if (!users.contains(user)) {
            log.error("Пользователя с id: {} не существует", user.getId());
            throw new NotFoundException("Пользователь не найден");
        }
        User oldUser = users.get(users.indexOf(user));
        if (user.getBirthday() != null && !user.getBirthday().equals(oldUser.getBirthday())) {
            log.debug("Дата рождения изменена с '{}' на '{}'", oldUser.getBirthday(), user.getBirthday());
            oldUser.setBirthday(user.getBirthday());
        }
        if (user.getName() != null && !user.getName().equals(oldUser.getName())) {
            if (user.getName().isBlank()) {
                log.debug("Имя пользователя изменено на '{}'", user.getEmail());
                oldUser.setName(user.getEmail());
            } else {
                log.debug("Имя пользователя изменено на '{}'", user.getName());
                oldUser.setName(user.getName());
            }
        }

        if (user.getEmail() != null && !user.getEmail().equals(oldUser.getEmail())) {
            for (User u : users) {
                if (u.getEmail().equals(user.getEmail())) {
                    log.error("Данный email: {} уже зарегистрирован", user.getEmail());
                    throw new AlreadyExistsException("Пользователь с таким email уже существует");
                }
            }
            log.debug("Email пользователя изменён с '{}' на '{}'", oldUser.getEmail(), user.getEmail());
            oldUser.setEmail(user.getEmail());
        }

        if (user.getLogin() != null && !user.getLogin().equals(oldUser.getLogin())) {
            for (User u : users) {
                if (u.getLogin().equals(user.getLogin())) {
                    log.error("Данный логин {} уже зарегистрирован", user.getLogin());
                    throw new AlreadyExistsException("Пользователь с таким логином уже существует");
                }
            }
            log.debug("Логин пользователя изменён с '{}' на '{}'", oldUser.getLogin(), user.getLogin());
            oldUser.setLogin(user.getLogin());
        }
        log.info("Пользователь с id: {} успешно обновлён", user.getId());
        return oldUser;
    }
}