package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.exeption.AlreadyExistsException;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@AllArgsConstructor
@Getter
@Slf4j
@Validated
public class UserServiceDb implements UserService {

    private final UserStorage storage;

    @Override
    public void addFriend(Long userId, Long friendId) {
        log.info("Попытка добавить друга с ID {} для пользователя с ID {}", friendId, userId);

        if (userId.equals(friendId)) {
            throw new IllegalArgumentException("Нельзя добавить самого себя в друзья");
        }

        User user = storage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
        User friend = storage.findById(friendId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + friendId + " не найден"));

        Set<Long> userFriends = user.getFriends();
        if (!userFriends.add(friendId)) {
            throw new AlreadyExistsException("Пользователь уже состоит в дружбе с " + friendId);
        }

        storage.addFriend(userId, friendId);
        log.info("Друг с ID {} успешно добавлен к пользователю с ID {}", friendId, userId);
    }

    @Override
    public void removeFriend(Long userId, Long friendId) {
        log.info("Попытка удалить друга с ID {} у пользователя с ID {}", friendId, userId);

        User user = storage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));

        User friend = storage.findById(friendId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + friendId + " не найден"));

        Set<Long> friends = user.getFriends();

        if (!friends.remove(friendId)) {
            return;
        }

        storage.removeFriend(userId,friendId);
        log.info("Друг с ID {} успешно удалён у пользователя с ID {}", friendId, userId);
    }

    @Override
    public List<User> getMutualFriends(Long userId, Long otherUserId) {
        log.info("Запрос на получение общих друзей между пользователями с ID {} и ID {}", userId, otherUserId);

        User user = storage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
        User otherUser = storage.findById(otherUserId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + otherUserId + " не найден"));

        Set<Long> mutualFriends = new HashSet<>(user.getFriends());
        mutualFriends.retainAll(otherUser.getFriends());

        return storage.findAllByIdIn(mutualFriends);
    }

    @Override
    public List<User> getFriends(Long userId) {
        User user = storage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
        return storage.findAllByIdIn(user.getFriends());
    }

    @Override
    public User createUser(User user) {
        Optional<User> existingByEmail = storage.findByEmail(user.getEmail());
        if (existingByEmail.isPresent()) {
            throw new AlreadyExistsException("Пользователь с email " + user.getEmail() + " уже существует");
        }
        Optional<User> existingByLogin = storage.findByLogin(user.getLogin());
        if (existingByLogin.isPresent()) {
            throw new AlreadyExistsException("Пользователь с логином " + user.getLogin() + " уже существует");
        }
        return storage.save(user);
    }

    @Override
    public User updateUser(User user) {
        User oldUser = storage.findById(user.getId())
                .orElseThrow(() -> new NotFoundException("Пользователь с id: " + user.getId() + " не существует"));

        if (user.getEmail() != null && !user.getEmail().equals(oldUser.getEmail())) {
            Optional<User> foundByEmail = storage.findByEmail(user.getEmail());
            if (foundByEmail.isPresent()) {
                throw new AlreadyExistsException("Юзер с таким email уже существует");
            }
            oldUser.setEmail(user.getEmail());
        }

        if (user.getLogin() != null && !user.getLogin().equals(oldUser.getLogin())) {
            Optional<User> foundByLogin = storage.findByLogin(user.getLogin());
            if (foundByLogin.isPresent()) {
                throw new AlreadyExistsException("Юзер с таким login уже существует");
            }
            oldUser.setLogin(user.getLogin());
        }

        if (user.getName() != null && !user.getName().equals(oldUser.getName())) {
            oldUser.setName(user.getName());
        }

        if (user.getBirthday() != null && !user.getBirthday().equals(oldUser.getBirthday())) {
            oldUser.setBirthday(user.getBirthday());
        }

        storage.save(oldUser);
        return oldUser;
    }

    @Override
    public List<User> getUsers() {
        return storage.findAllUsers();
    }

    @Override
    public User findUserById(Long id) {
        return storage.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id: " + id + " не найден"));
    }
}